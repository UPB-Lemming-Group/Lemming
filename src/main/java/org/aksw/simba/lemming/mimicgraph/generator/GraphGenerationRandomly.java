package org.aksw.simba.lemming.mimicgraph.generator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import toools.set.DefaultIntSet;
import toools.set.IntSet;

import com.carrotsearch.hppc.BitSet;

public class GraphGenerationRandomly extends AbstractGraphGeneration implements IGraphGeneration{

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphGenerationRandomly.class);
	private int maxIterationFor1EdgeColo ;
	
	public GraphGenerationRandomly(int iNumberOfVertices,
			ColouredGraph[] origGrphs) {
		super(iNumberOfVertices, origGrphs);
		maxIterationFor1EdgeColo = Constants.MAX_ITERATION_FOR_1_COLOUR;
	}

	public ColouredGraph generateGraph(){
		if(Constants.SINGLE_THREAD){
			generateGraphSingleThread();
		}else{
			generateGraphMultiThreads();
		}
		return mMimicGraph;
	}
	
	
	private void generateGraphMultiThreads(){
		
		//exploit all possible threads
		int iNumberOfThreads = getDefaultNoOfThreads();
		//int iNumberOfThreads = 4;
		List<IntSet> lstAssignedEdges = getAssignedListEdges(iNumberOfThreads);
		ExecutorService service = Executors.newFixedThreadPool(iNumberOfThreads);
		for(int i = 0 ; i < lstAssignedEdges.size() ; i++){
			final IntSet setOfEdges = lstAssignedEdges.get(i);
			final Set<BitSet> setAvailableVertexColours = mMapColourToVertexIDs.keySet();
			
			Runnable worker = new Runnable() {
				@Override
				public void run() {
					//max iteration of 1 edge
					int maxIterationFor1Edge = Constants.MAX_EXPLORING_TIME;
					//track the index of previous iteration
					int iOldIndex = -1;
					//set of process edges
					int[] arrOfEdges = setOfEdges.toIntArray();
					// set of failed edge colours
					Set<BitSet> failedEdgeColours = new HashSet<BitSet>();
					
					for(int j = 0 ; j < arrOfEdges.length; ){
						//get an edge id
						int fakeEdgeId = arrOfEdges[j];
						BitSet edgeColo = getEdgeColour(fakeEdgeId);
						
						if(failedEdgeColours.contains(edgeColo)){
							//skip the edge that has failed edge colour
							j++;
							continue;
						}
						
						if(iOldIndex != j){
							maxIterationFor1Edge = Constants.MAX_EXPLORING_TIME;
							iOldIndex = j;
						}else{
							if(maxIterationFor1Edge == 0){
								LOGGER.error("Could not create an edge of "
										+ edgeColo
										+ " colour since it could not find any approriate vertices to connect.");						
								
								failedEdgeColours.add(edgeColo);
								j++;
								continue;
							}
						}
						
						
						boolean isFoundVerticesConnected = false;
						//get potential tail colours
						Set<BitSet> setTailColours = mColourMapper.getTailColoursFromEdgeColour(edgeColo);
						setTailColours.retainAll(setAvailableVertexColours);

						/*
						 * in case there is no tail colours => the edge colour should not 
						 * be considered again
						 */
						if(setTailColours.size() == 0){
							failedEdgeColours.add(edgeColo);
							j++;
							continue;
						}
						
						//get random a tail colour
						BitSet[] arrTailColours = setTailColours.toArray(new BitSet[0]);
						BitSet tailColo = arrTailColours[mRandom.nextInt(arrTailColours.length)];	
						Set<BitSet> setHeadColours = mColourMapper.getHeadColours(tailColo, edgeColo);
						
						if(setHeadColours == null || setHeadColours.size() == 0){
							maxIterationFor1Edge--;
							continue;
						}
						setHeadColours.retainAll(setAvailableVertexColours);
						
						if(setHeadColours.size() == 0){
							maxIterationFor1Edge--;
							continue;
						}
						
						BitSet [] arrHeadColours = setHeadColours.toArray(new BitSet[0]);
						
						BitSet headColo = arrHeadColours[mRandom.nextInt(arrHeadColours.length)];
						
						//get set of tail ids and head ids
						IntSet setTailIDs = new DefaultIntSet();
						IntSet setHeadIDs = new DefaultIntSet();
						
						if(mMapColourToVertexIDs.containsKey(tailColo)){
							setTailIDs = mMapColourToVertexIDs.get(tailColo).clone();
						}
						
						if(mMapColourToVertexIDs.containsKey(headColo)){
							setHeadIDs = mMapColourToVertexIDs.get(headColo).clone();
						}
						
						if(setTailIDs!= null && setTailIDs.size()> 0 && setHeadIDs!=null && setHeadIDs.size()> 0){
							int[] arrTailIDs = setTailIDs.toIntArray();
							int tailId = -1;
							int iAttemptToGetTailIds = 1000;
							while(iAttemptToGetTailIds > 0){
								tailId = arrTailIDs[mRandom.nextInt(arrTailIDs.length)];
								if(!mReversedMapClassVertices.containsKey(tailColo))
									break;
								tailId = -1;
								iAttemptToGetTailIds --;	
							}
							
							if(tailId ==-1){
								maxIterationFor1Edge--;
								continue;
							}
							
							IntSet tmpSetOfConnectedHeads = getConnectedHeads(tailId, edgeColo);
							if(tmpSetOfConnectedHeads!= null && tmpSetOfConnectedHeads.size() >0  ){
								//int[] arrConnectedHeads = tmpSetOfConnectedHeads.toIntArray(); 
								for(int connectedHead: tmpSetOfConnectedHeads.toIntegerArrayList()){
									if(setHeadIDs.contains(connectedHead))
										setHeadIDs.remove(connectedHead);
								}
							}
							
							if(setHeadIDs.size() == 0 ){
								maxIterationFor1Edge--;
								continue;
							}
							
							int[] arrHeadIDs = setHeadIDs.toIntArray();
							
							int headId = arrHeadIDs[mRandom.nextInt(arrHeadIDs.length)];
							isFoundVerticesConnected = connectIfPossible(tailId, headId, edgeColo);
							if(isFoundVerticesConnected){
								j++;
								continue;
							}
						}
						
						maxIterationFor1Edge--;
						if (maxIterationFor1Edge == 0) {
							LOGGER.error("Could not create an edge of "
									+ edgeColo
									+ " colour since it could not find any approriate vertices to connect.");					
							
							failedEdgeColours.add(edgeColo);
							j++;
						}
						
					}//end of for of edge ids
				}
			};
			service.execute(worker);
		}
		
		service.shutdown();
		try {
			service.awaitTermination(48, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			LOGGER.error("Could not shutdown the service executor! Be carefule");
			e.printStackTrace();
		};
	}
	
	
	private void generateGraphSingleThread(){
		
		/*
		 * mMapColourToEdgeIDs contains only normal edges (not datatype property edges and
		 * rdf:type edges)
		 */
		Set<BitSet> keyEdgeColo = mMapColourToEdgeIDs.keySet();
		Set<BitSet> setAvailableVertexColours = mMapColourToVertexIDs.keySet();
		int iColoCounter = 0;
		for(BitSet edgeColo : keyEdgeColo){
			iColoCounter++;
			Set<BitSet> setTailColours = mColourMapper.getTailColoursFromEdgeColour(edgeColo);
			setTailColours.retainAll(setAvailableVertexColours);
			
			BitSet[] arrTailColours = setTailColours.toArray(new BitSet[0]);
			
			/* the setFakeEdgeIDs helps us to know how many edges existing
			 * in a specific edge's colour*/ 
			IntSet setFakeEdgeIDs = mMapColourToEdgeIDs.get(edgeColo);
			// use each edge to connect vertices
			int i = 0 ;
			LOGGER.info("Generated edges for "+edgeColo +" edge colour ("+iColoCounter+"/"+keyEdgeColo.size()+")");
			while(i < setFakeEdgeIDs.size()){
					
				boolean isFoundVerticesConnected = false;
				
				BitSet tailColo = arrTailColours[mRandom.nextInt(arrTailColours.length)];	
				Set<BitSet> setHeadColours = mColourMapper.getHeadColours(tailColo, edgeColo);
				
				if(setHeadColours == null || setHeadColours.size() ==0){
					continue;
				}
				
				setHeadColours.retainAll(setAvailableVertexColours);
				
				if(setHeadColours.size() ==0)
					continue;
				
				BitSet [] arrHeadColours = setHeadColours.toArray(new BitSet[0]);
				
				BitSet headColo = arrHeadColours[mRandom.nextInt(arrHeadColours.length)];
				IntSet setTailIDs = new DefaultIntSet();
				IntSet setHeadIDs = new DefaultIntSet();
				
				if(mMapColourToVertexIDs.containsKey(tailColo)){
					setTailIDs = mMapColourToVertexIDs.get(tailColo).clone();
				}
				
				if(mMapColourToVertexIDs.containsKey(headColo)){
					setHeadIDs = mMapColourToVertexIDs.get(headColo).clone();
				}
				
				if(setTailIDs!= null && setTailIDs.size()> 0 && setHeadIDs!=null && setHeadIDs.size()> 0){
					int[] arrTailIDs = setTailIDs.toIntArray();
					int tailId = -1;
					while(true){
						tailId = arrTailIDs[mRandom.nextInt(arrTailIDs.length)];
						if(!mReversedMapClassVertices.containsKey(tailColo))
							break;
					}
					
					int[] arrConnectedHeads = getConnectedHeads(tailId,edgeColo).toIntArray(); 
					for(int connectedHead: arrConnectedHeads){
						if(setHeadIDs.contains(connectedHead))
							setHeadIDs.remove(connectedHead);
					}
					
					if(setHeadIDs.size() == 0 ){
						continue;
					}
					
					int[] arrHeadIDs = setHeadIDs.toIntArray();
					
					int headId = arrHeadIDs[mRandom.nextInt(arrHeadIDs.length)];
					isFoundVerticesConnected = connectIfPossible(tailId, headId, edgeColo);
					if(isFoundVerticesConnected){
						isFoundVerticesConnected = true;	
						i++;
						
						LOGGER.info("   -- Add "+i+"/"+setFakeEdgeIDs.size()+" edges");
						
					}
//						else{
//							System.err.println("Found same vertices to connect");
//						}
				}
//					else{
//						System.err.println("Could not find any vertices with the tail's or head's colours!");
//						LOGGER.warn("Could not find any vertices with the tail's or head's colours!");
//					}
				
				if (!isFoundVerticesConnected) {
					maxIterationFor1EdgeColo--;
					if (maxIterationFor1EdgeColo == 0) {
						LOGGER.error("Could not create "
								+ (setFakeEdgeIDs.size() - i)
								+ " edges in the "
								+ edgeColo
								+ " colour since it could not find any approriate vertices to connect.");
						
						System.err.println("Could not create "
								+ (setFakeEdgeIDs.size() - i)
								+ " edges in the "
								+ edgeColo
								+ " colour since it could not find any approriate vertices to connect.");
						break;
					}
				}
			}
			
			maxIterationFor1EdgeColo = Constants.MAX_ITERATION_FOR_1_COLOUR;
		}
	}
	
}
