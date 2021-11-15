import java.util.ArrayList;

/**
 * This class contains methods which perform various operations on a layered linked
 * list to simulate transit
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class Transit {
	/**
	 * Makes a layered linked list representing the given arrays of train stations, bus
	 * stops, and walking locations. Each layer begins with a location of 0, even though
	 * the arrays don't contain the value 0.
	 * 
	 * @param trainStations Int array listing all the train stations
	 * @param busStops Int array listing all the bus stops
	 * @param locations Int array listing all the walking locations (always increments by 1)
	 * @return The zero node in the train layer of the final layered linked list
	 */
	public static TNode makeList(int[] trainStations, int[] busStops, int[] locations) {
		// WRITE YOUR CODE HERE
		TNode down2 = new TNode();
		TNode down = new TNode(0, null, down2);
		TNode root = new TNode(0, null, down);
		TNode ptr = root;
		TNode ptrb = root.down;
		TNode ptrl = root.down.down;

		for(int i = 0; i < locations.length; i++){
			TNode temp = new TNode(locations[i]);
			ptrl.next = temp;
			ptrl = ptrl.next;
		}

		for(int i = 0; i < busStops.length; i++){
			int val = busStops[i];
			TNode p = down2;
			while(p.location != val){
				p=p.next;
			}
			TNode temp = new TNode(val,null,p);
			
			ptrb.next = temp;

			ptrb= ptrb.next;
		}

		for(int i = 0; i < trainStations.length; i++){
			int val = trainStations[i];
			TNode p2 = down;
			while(p2.location != val){
				p2 = p2.next;
			}

			TNode temp = new TNode(val, null, p2);
			ptr.next = temp;
			
		 	ptr = ptr.next;

		}

		

		
		
		return root;
	}
	
	/**
	 * Modifies the given layered list to remove the given train station but NOT its associated
	 * bus stop or walking location. Do nothing if the train station doesn't exist
	 * 
	 * @param trainZero The zero node in the train layer of the given layered list
	 * @param station The location of the train station to remove
	 */
	public static void removeTrainStation(TNode trainZero, int station) {
		TNode ptr = trainZero;

		while(ptr != null){
			if(ptr.next != null && ptr.next.location == station){
				ptr.next = ptr.next.next;
			}
			ptr = ptr.next;
		}

	}

	/**
	 * Modifies the given layered list to add a new bus stop at the specified location. Do nothing
	 * if there is no corresponding walking location.
	 * 
	 * @param trainZero The zero node in the train layer of the given layered list
	 * @param busStop The location of the bus stop to add
	 */
	public static void addBusStop(TNode trainZero, int busStop) {
		TNode ptr = trainZero.down;
		TNode walk = ptr.down;
		TNode newb = new TNode(busStop);

		int max = 0;
		while(walk != null){
			max = walk.location;
			walk = walk.next;
		}

		if(busStop <= max){
			while(ptr != null){
			
				if(busStop > ptr.location && ptr.next == null){
					ptr.next = newb;
					newb.next = null;
					TNode p = trainZero.down.down;
					while(p.location != busStop){
						p = p.next;
					}
					newb.down = p;
				}
				if(ptr.location < busStop && ptr.next.location > busStop){
					TNode temp = ptr.next;
					ptr.next = newb;
					newb.next = temp;
					TNode p = trainZero.down.down;
					while(p.location != busStop){
						p = p.next;
					}
					newb.down = p;
				}
	
				ptr = ptr.next;
			}
		}
		

	}
	
	/**
	 * Determines the optimal path to get to a given destination in the walking layer, and 
	 * collects all the nodes which are visited in this path into an arraylist. 
	 * 
	 * @param trainZero The zero node in the train layer of the given layered list
	 * @param destination An int representing the destination
	 * @return
	 */
	public static ArrayList<TNode> bestPath(TNode trainZero, int destination) {
		// WRITE YOUR CODE HERE
		ArrayList<TNode> root = new ArrayList<>();
		TNode ptr = trainZero;

		while(ptr != null){
			if(ptr.next == null){
				root.add(ptr);
				break;
			}
			if((ptr.location <= destination) && (ptr.next.location > destination)){
				root.add(ptr);
				break;
			}
			root.add(ptr);
			ptr = ptr.next;
		}
		TNode ptrb = ptr.down;

		while(ptrb != null){
			if(ptrb.next == null){
				root.add(ptrb);
				break;
			}
			if((ptrb.location <= destination) && ptrb.next.location > destination){
				root.add(ptrb);
				break;
			}
			root.add(ptrb);
			ptrb = ptrb.next;
		}
		TNode ptrl = ptrb.down;

		while(ptrl != null){
			if(ptrl.next == null){
				root.add(ptrl);
				break;
			}
			if((ptrl.location <= destination) && (ptrl.next.location > destination)){
				root.add(ptrl);
				break;
			}
			root.add(ptrl);
			ptrl = ptrl.next;
		}
		return root;
	}

	/**
	 * Returns a deep copy of the given layered list, which contains exactly the same
	 * locations and connections, but every node is a NEW node.
	 * 
	 * @param trainZero The zero node in the train layer of the given layered list
	 * @return
	 */
	public static TNode duplicate(TNode trainZero) {
		// WRITE YOUR CODE HERE
		TNode down2 = new TNode();
		TNode down = new TNode(0, null, down2);
		TNode newT = new TNode(0, null, down);

		TNode newTptr = newT;
		TNode newTptrd = newT.down;
		TNode newTptrdd = newT.down.down;

		TNode ptr = trainZero.next;
		TNode ptrb = trainZero.down.next;
		TNode ptrw = trainZero.down.down.next;

		while(ptrw != null){
			TNode temp = new TNode(ptrw.location);
			newTptrdd.next = temp;

			newTptrdd = newTptrdd.next;
			ptrw = ptrw.next;
		}

		while(ptrb != null){

			TNode p = newT.down.down.next;
			while(p.location != ptrb.location){
				p = p.next;
			}
			TNode tt = new TNode(p.location, null, p);

			newTptrd.next = tt;
			


			newTptrd = newTptrd.next;
			ptrb = ptrb.next;
		}

		while(ptr != null){
			TNode p = newT.down.next;
			while(p.location != ptr.location){
				p = p.next;
			}
			TNode tt = new TNode(p.location, null, p);

			newTptr.next = tt;
			
			


			newTptr = newTptr.next;
			ptr = ptr.next;
		}

		return newT;
	}

	/**
	 * Modifies the given layered list to add a scooter layer in between the bus and
	 * walking layer.
	 * 
	 * @param trainZero The zero node in the train layer of the given layered list
	 * @param scooterStops An int array representing where the scooter stops are located
	 */
	public static void addScooter(TNode trainZero, int[] scooterStops) {
		// WRITE YOUR CODE HERE
		TNode ptr = trainZero;
		TNode bus = trainZero.down;
		TNode walk = ptr.down.down;
		TNode head = new TNode(0,null,walk);
		ptr.down.down = head;
		TNode headptr = head;
		 
		for(int i =0; i < scooterStops.length; i++){
			int val = scooterStops[i];
			TNode temp = new TNode(val);
			headptr.next = temp;
			headptr = headptr.next;

			while(walk.location != val){
				walk = walk.next;
			}
			headptr.down = walk;
		}
		while(bus != null){
			TNode hh = head;
			while(bus.location!= hh.location){
				hh = hh.next;
			}
			bus.down = hh;
			bus = bus.next;
		}


	}

}