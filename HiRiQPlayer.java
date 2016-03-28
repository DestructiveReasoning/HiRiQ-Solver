public class HiRiQPlayer {

/**
 * Copyright 2016 Harley Wiltzer
 * HiRiQ (modified Peg Solitaire) Solver
 * To solve a puzzle, create a new HiRiQSolver object, and use its solveHiRiQ(boolean[]) method.
 */

	public static final byte[][] triplets = {
		{6,7,8},{6,13,20},{13,14,15},{20,21,22},{7,8,9},{7,14,21},{14,15,16},{21,22,23},
		{0,1,2},{0,3,8},{3,4,5},{3,8,15},{8,9,10},{8,15,22},{15,16,17},{15,22,27},{22,23,24},
		{22,27,30},{27,28,29},{30,31,32},{1,4,9},{4,9,16},{9,10,11},{9,16,23},{16,17,18},
		{16,23,28},{23,24,25},{23,28,31},{2,5,10},{5,10,17},{10,11,12},{10,17,24},{17,18,19},
		{17,24,29},{24,25,26},{24,29,32},{11,18,25},{12,19,26}
	};

	public static void main(String[] args) {

	class BitField {
		/**
		 * This is a data structure that I made to allow constant-time search and add.
		 * It is used to keep track of the states of the puzzle that have been seen already.
		 * It creates enough bytes to store one bit for each possible board state, and does 
		 * bit manipulation on the bytes to add elements or check if they've already been encountered.
		 */
		private byte[] field; //Stores the bits and bytes.

		public BitField(long range) {
			//Takes in the amount of bits required. Right shifts by 3 to figure out how many bytes are required.
			field = new byte[(int)(range >> 3) + 1];
		}

		public void add(long n) {
			//Figures out which byte to access by right-shifting by 3 (dividing by 8).
			//Figure out which bit to alter by ANDing with 7 (Learned in ECSE 221).
			//Uses OR to set the bit to 1.
			field[(int)(n >> 3)] |= (1 << (n & 7));
		}

		public boolean search(long n) {
			//Similar logic to add(). Checks if appropriate bit is 1.
			return ((field[(int)(n >> 3)] & (1 << (n & 7))) == (1 << (n & 7)));
		}

		public void clear() {
			//Resets all bits to 0.
			for(int c = 0; c < field.length; c++) field[c] = (byte)0;
		}
	}
	class HSLLNode<T> {
		//This class is made for the funtioning of the HSLinkedList class.
		private T current; //Stores the current element.
		public HSLLNode<T> next; //Pointer to the next element in the linked list.

		public HSLLNode(T current) {
			this.current = current;
			this.next = null;
		}

		public T get() { return current; };
	}
	class HSLinkedList<T> {
		//Singly Linked List class.
		HSLLNode<T> head; //Pointer to the first element in the list.
		HSLLNode<T> tail; //Pointer to the last element in the list.
		int size; //Stores the size of the list.

		public HSLinkedList() {
			size = 0;
			head = null;
			tail = null;
		}

		public void addLast(T t) {
			//Adds element to the end of the linked list in constant time.
			HSLLNode<T> h = new HSLLNode<T>(t);
			if(size == 0) 
			{
				head = h; //If size is 0, make sure to initialize head
				tail = h;
				size++;
				return;
			}
			tail.next = h;
			tail = h;
			size++;
		}

		public void addFirst(T t) {
			//Adds element to the head of the linked list in constant time.
			HSLLNode<T> h = new HSLLNode<T>(t);
			if(size == 0) tail = h; //If the size of the list is 0, make sure to initialize tail.
			h.next = head;
			head = h;
			size++;
		}

		public void removeFirst() {
			//Removes the first element of the linked list in constant time.
			head = head.next;
			size--;
		}

		public void print() {
			//Helper method for printing the contents of the linked list. Used for debugging.
			System.out.print("[");
			HSLLNode<T> iterator = head;
			while(iterator.next != null) {
				System.out.print(iterator.get() + ", ");
				iterator = iterator.next;
			}
			System.out.println(iterator.get() + "]");
		}
	}
	class HiRiQ {
	//int is used to reduce storage to a minimum...
	  public int config;
	  public byte weight;

	//initialize the configuration to one of 4 START setups n=0,1,2,3
	  HiRiQ(byte n)
	  {
		  if (n==0)
		   {config=65536/2;weight=1;}
		  else
			  if (n==1)
			  {config=1626;weight=6;}
			  else
				  if (n==2)
				  {config=-1140868948; weight=10;}
				  else
					  if (n==3)
					  {config=-411153748; weight=13;}
		  				else
							  {config=-2147450879; weight=32;}
	  }
	  
	  //initialize the configuration to one of 4 START setups n=0,10,20,30

	  boolean IsSolved()
	  {
		  return( (config==65536/2) && (weight==1) );
	  }

	//transforms the array of 33 booleans to an (int) cinfig and a (byte) weight.
	  public void store(boolean[] B)
	  {
	  int a=1;
	  config=0;
	  weight=(byte) 0;
	  if (B[0]) {weight++;}
	  for (int i=1; i<32; i++)
	   {
	   if (B[i]) {config=config+a;weight++;}
	   a=2*a;
	   }
	  if (B[32]) {config=-config;weight++;}
	  }

	//transform the int representation to an array of booleans.
	//the weight (byte) is necessary because only 32 bits are memorized
	//and so the 33rd is decided based on the fact that the config has the
	//correct weight or not.
	  public boolean[] load(boolean[] B)
	  {
	  byte count=0;
	  int fig=config;
	  B[32]=fig<0;
	  if (B[32]) {fig=-fig;count++;}
	  int a=2;
	  for (int i=1; i<32; i++)
	   {
	   B[i]= fig%a>0;
	   if (B[i]) {fig=fig-a/2;count++;}
	   a=2*a;
	   }
	  B[0]= count<weight;
	  return(B);
	  }
	  
	//prints the int representation to an array of booleans.
	//the weight (byte) is necessary because only 32 bits are memorized
	//and so the 33rd is decided based on the fact that the config has the
	//correct weight or not.
	  public void printB(boolean Z)
	  {if (Z) {System.out.print("[ ]");} else {System.out.print("[@]");}}
	  
	  public void print()
	  {
	  byte count=0;
	  int fig=config;
	  boolean next,last=fig<0;
	  if (last) {fig=-fig;count++;}
	  int a=2;
	  for (int i=1; i<32; i++)
	   {
	   next= fig%a>0;
	   if (next) {fig=fig-a/2;count++;}
	   a=2*a;
	   }
	  next= count<weight;
	  
	  count=0;
	  fig=config;
	  if (last) {fig=-fig;count++;}
	  a=2;

	  System.out.print("      ") ; printB(next);
	  for (int i=1; i<32; i++)
	   {
	   next= fig%a>0;
	   if (next) {fig=fig-a/2;count++;}
	   a=2*a;
	   printB(next);
	   if (i==2 || i==5 || i==12 || i==19 || i==26 || i==29) {System.out.println() ;}
	   if (i==2 || i==26 || i==29) {System.out.print("      ") ;};
	   }
	   printB(last); System.out.println() ;

	  }
	}//End class
		class HTreeNode<T>{
			/**
			 * Although solver algorithm doesn't actually use a tree, it uses a 
			 * 'virtual' tree (see class HTree) which uses these tree-like nodes.
			 */
			T current; //Stores the current element.
			byte[] move; //Stores the move that occurs to lead to this node.
			HTreeNode<T> parent; //Stores the node in the move sequence that comes before this node.

			public HTreeNode(T current, HTreeNode<T> parent, byte[] move) {
				this.current = current;
				this.parent = parent;
				this.move = move;
			}
		}
		class HTree{
			/**
			 * This class represents a 'virtual' tree structure that implements a Depth-First-Search algorithm
			 * It is made up of HTreeNodes.
			 */
			//The following number MINIMUM is made to take into account the fact that the config member of the HiRiQ class is not always positive.
			//MINIMUM represents the lowest negative value a config value can take on. This will be useful for adding states to the BitField.
			public static final long MINIMUM = 2147483648l;
			HTreeNode<HiRiQ> root; //Stores the node representing the initial configuration of the puzzle.
			BitField configsDiscovered; //Keeps track of which configs have already been encountered.
			private boolean[] current; //Stores the booleans that represent the current board state.
			HiRiQ next; //This will be used to add new HiRiQ's to the stack.
			HSLinkedList<HTreeNode<HiRiQ>> stack; //Stack structure implemented with a HSLinkedList.
			int bMoveCheck; //Stores the lowest stack size for which B moves should be considered.
			String solution; //Stores the String representing the sequence of moves that lead to a solution.
			public HTree(HiRiQ initialConfig) {
				this.root = new HTreeNode<HiRiQ>(initialConfig,null,new byte[3]);
				configsDiscovered = new BitField((long)Math.pow(2,33)); //There are 2^33 possible board configurations -> BitField must store 2^33 bits.
				configsDiscovered.add(root.current.config + MINIMUM); //Always add MINIMUM for BitField to take negative configs into account.
				current = new boolean[33];
				next = new HiRiQ((byte)0);
				stack = new HSLinkedList<HTreeNode<HiRiQ>>();
				stack.addFirst(root); 
				bMoveCheck = 1; //By default, only check for B moves when stack size is 1. (Reduces the amount of B move checks).
				solution = "";
			}

			public String run() {
				//Wrapper method for the recursive DFS method.
				//Initializes everything and runs DFS.
				root.current.print();
				System.out.println();
				System.out.println(">>Attempting to find solution...");
				solution = "";
				bMoveCheck = 1;
				stack = new HSLinkedList<HTreeNode<HiRiQ>>();
				stack.addFirst(root);
				configsDiscovered.clear();
				configsDiscovered.add(root.current.config + MINIMUM);
				recursiveDFS(false);
				if(solution == "") return "No Solution";
				return solution;
			}

			public void recursiveDFS(boolean verbose) {
				//This is the depth-first search algorithm
				//The verbose parameter is for debugging.
				if(!solution.equals("")) return; //Solution is only modified when a solution has been found. Thus, if solution isn't empty, return.
				byte moves = (byte)0;
				if(verbose) System.out.println("Stack size: " + stack.size);
				if(verbose) stack.head.current.current.print();
				for(byte[] t : triplets) {
					if(!solution.equals("")) return; //If a solution has already been found, return.
					stack.head.current.current.load(current); //Load the boolean array of the first element in the stack
					byte c = legalMove(current,t);
					if(c == 0 || c > 2) { //legalMove returns 1 or 2 for W moves.
						if(verbose) System.out.println("No legal W move found");
						//Continue to next triplet if current one doesn't allow W move.
						continue;
					}
					applyMove(current,t); //Applies substitution on boolean array
					next.store(current);
					byte bool = current[t[1]] ? (byte)1 : (byte)0; //Stores type of move
					//legalMove returns even number if substitution occurs from lower index to greater index.
					byte[] move = (c % 2 != 0) ? encodeMove(t[0],t[2],bool) : encodeMove(t[2],t[0],bool); //Decides order of indices for the move
					if(next.IsSolved()) {
						System.out.println("Found solution!");
						next.print();
						System.out.println("Moves: " + stack.size); //Displays amount of moves in solution
						solution = decodeMove(move); //Starts modifying solution String
						HTreeNode<HiRiQ> iterator = stack.head.current;
						while(iterator.parent != null) {
							solution = decodeMove(iterator.move) + "\n" + solution;
							iterator = iterator.parent;
						}
						return;
					}
					if(!configsDiscovered.search(next.config + MINIMUM)){ //Checks if next state has already been seen
						if(verbose) System.out.println("Found W move: " + getTripletString(t));
						configsDiscovered.add(next.config + MINIMUM); //Adds new state to BitField
						HiRiQ nextCopy = new HiRiQ((byte)0);
						nextCopy.config = next.config;
						nextCopy.weight = next.weight;
						stack.addFirst(new HTreeNode<HiRiQ>(nextCopy,stack.head.current,move)); //Adds new state to front of stack
						moves++; //Increments amount of moves for current node.
						recursiveDFS(verbose); //Recursively calls the function (note that the head of the stack has been changed).
					}
					else if(verbose) System.out.println("Found repetition");
				}
				if(stack.size == bMoveCheck) {
					//If low down enough in 'tree', check for a B move
					bMoveCheck++; //Allow B move check to be one level higher.
					if(verbose) System.out.println("Triplets: " + triplets.length);
					for(byte[] b : triplets) {
						//Same logic as previous W move check.
						//No need to check for a solution because there are always at least 2 white tiles after a B move.
						stack.head.current.current.load(current);
						byte c = legalMove(current,b);
						if(c == 0 || c <= 2) {
							if(verbose) System.out.println("No legal B move found");
							continue;
						}
						if(verbose) System.out.println("Potential B move found");
						applyMove(current,b);
						next.store(current);
						byte bool = current[b[1]] ? (byte)1 : (byte)0;
						byte[] move = (c % 2 != 0) ? encodeMove(b[0],b[2],bool) : encodeMove(b[2],b[0],bool);
						if(!configsDiscovered.search(next.config + MINIMUM)){
							if(verbose) System.out.println("Found B move: " + getTripletString(b));
							configsDiscovered.add(next.config + MINIMUM);
							HiRiQ nextCopy = new HiRiQ((byte)0);
							nextCopy.config = next.config;
							nextCopy.weight = next.weight;
							stack.addFirst(new HTreeNode<HiRiQ>(nextCopy,stack.head.current,move));
							recursiveDFS(verbose);
							moves++;
						}
						else if(verbose) System.out.println("Found B move that leads to repetition");
					}
					if(verbose) System.out.println("No more triplets");
				}
				if(stack.size > 1) {
					stack.removeFirst();
					if(verbose) {
						System.out.println("***Backtracking");
						if(verbose) stack.head.current.current.print();
						if(verbose) System.out.println();
					}
				}
				else return;
			}
		}
		class HiRiQSolver {
			public String solveHiRiQ(boolean[] board) {
				HiRiQ puzzle = new HiRiQ((byte)0);
				puzzle.store(board);
				HTree tree = new HTree(puzzle);
				String solution = tree.run();
				return solution;
			}
		}

		boolean[] B=new boolean[33];
		boolean[] C=new boolean[33];
		boolean[] D=new boolean[33];
		HiRiQ W=new HiRiQ((byte) 0) ;
//		W.print(); System.out.println(W.IsSolved());
		HiRiQ X=new HiRiQ((byte) 1) ;
//		X.print(); System.out.println(X.IsSolved());
		HiRiQ Y=new HiRiQ((byte) 2) ;
//		Y.print(); System.out.println(Y.IsSolved());
		HiRiQ Z=new HiRiQ((byte) 3) ;
//		Z.print(); System.out.println(Z.IsSolved());
		B=Z.load(B);
		for (int i=0; i<33; i++){B[i]= !B[i];};
//		Z.store(B);
//		Z.print(); System.out.println(Z.IsSolved());
		System.out.println();

		//Testing
		HiRiQ test = new HiRiQ((byte) 0);
		test = new HiRiQ((byte) 4);
		//This is how to use solver:
		HiRiQSolver solver = new HiRiQSolver();
		boolean [] board = new boolean[33];
		String solution = solver.solveHiRiQ(test.load(board));
		System.out.println(solution);
	}

	public static byte[] encodeMove(byte b, byte e, byte t) {
		//Inserts move parameters into byte array
		byte[] move = new byte[3];
		move[0] = b;
		move[1] = e;
		move[2] = t;
		return move;
	}
	public static String decodeMove(byte[] move) {
		//Returns string representing a byte[] move.
		byte b = move[0];
		byte e = move[1];
		char type = (move[2] == 0) ? 'W' : 'B';
		return "" + b + type + e;
	}
	public static byte legalMove(boolean [] board, byte[] triplet) {
		//Takes in a boolean[] that represents the current board, and another that represents a triplet on the board.
		//Returns a byte representing what move can be made.
		if(board[triplet[0]] == board[triplet[1]] && board[triplet[1]] == board[triplet[2]]) return 0; //No move.
		if(board[triplet[0]] == board[triplet[1]] && board[triplet[0]]) return 1; 	//W move from low index to high index.
		if(board[triplet[1]] == board[triplet[2]] && board[triplet[1]]) return 2; 	//W move from high index to low index.
		if(board[triplet[0]] == board[triplet[1]]) return 3;						//B move from low index to high index.
		if(board[triplet[1]] == board[triplet[2]]) return 4;						//B move from high index to low index.
		return 0; //No move.
	}
	public static void applyMove(boolean[] board, byte[] triplet) {
		//Modifies the current board to reflect the appropriate substitution on the given triplet.
		//Only one move can be made on a triplet for each state.
		//If a move is possible, all elements within the triplet are 'flipped'.
		board[triplet[0]] = !board[triplet[0]];
		board[triplet[1]] = !board[triplet[1]];
		board[triplet[2]] = !board[triplet[2]];
	}
	public static String getTripletString(byte[] triplet) {
		//Returns a string representing the three values in a byte array. Used for debugging.
		return "(" + triplet[0] + ", " + triplet[1] + ", " + triplet[2] + ")";
	}
	public static String getTripletString(boolean[] triplet) {
		//Returns string representing 3 boolean array. Used for debugging.
		return "(" + triplet[0] + ", " + triplet[1] + ", " + triplet[2] + ")";
	}
}

