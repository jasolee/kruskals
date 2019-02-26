/*
CMU 15-451 Kruskal's MST Search Algorithm using Tree Based Path Compression
Jason Lee
dongyool@andrew.cmu.edu
*/


import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.lang.*;

public class Mst {
    
    //tree implementation
    public static class Node<T> {
        private List<Node<T>> children = new ArrayList<Node<T>>();
        private Node<T> parent = null;
        private T data = null;
        private int rank = 0;

        public Node(T data) {
            this.data = data;
        }

        public List<Node<T>> getChildren() {
            return children;
        }
        
        public Node<T> getParent() {
            return this.parent;
        }

        public void setParent(Node<T> parent) {
            this.parent = parent;
        }

        public void addChild(Node<T> child) {
            this.children.add(child);
        }

        public T getData() {
            return this.data;
        }

        public boolean isRoot() {
            return (this.parent == null);
        }

        public boolean isLeaf() {
            return this.children.isEmpty();
        }
        
        public int getRank() {
            return this.rank;
        }
        
        public void upRank() {
            this.rank ++;
        }

    }
    
    //create singleton node
    public static Node makeSet(Integer x) {
        Node<Integer> myNode = new Node(x);
        return myNode;
    }
    
    // returns root node
    public static Node<Integer> find(Node<Integer> aNode) {
        ArrayList<Node<Integer>> nodesOnPath = new ArrayList<Node<Integer>>();
        Node<Integer> root = aNode;
        while(!root.isRoot()) {
            nodesOnPath.add(root);
            root = root.getParent();
        }
//        ArrayList<Node<Integer>> nodesToCompress = new ArrayList<Node<Integer>>(nodesOnPath.subList(0, nodesOnPath.size()-1));
        compressPath(root, nodesOnPath);
        return root;
    }
    
    public static Integer getDepth(Node<Integer> aNode) {
        int depth = 0;
        Node<Integer> root = aNode;
        while(!root.isRoot()) {
            root = root.getParent();
            depth ++;
        }
        
        return depth;
    }
    //path compression to reduce runtime when searching (find)
    public static void compressPath(Node<Integer> parentNode, ArrayList<Node<Integer>> nodes) {
        for (Node<Integer> aNode : nodes) {
            aNode.setParent(parentNode);
            parentNode.addChild(aNode);
        }
    }
    //union by rank
    public static void union(Node<Integer> root1, Node<Integer> root2) {
        int r1 = root1.getRank();
        int r2 = root2.getRank();
        
        if (r1 > r2) {
            root1.addChild(root2);
            root2.setParent(root1);
        } else if (r1 < r2) {
            root2.addChild(root1);
            root1.setParent(root2);
        } else {
            if (root1.getData() < root2.getData()) {
                root1.addChild(root2);
                root2.setParent(root1);
                root1.upRank();            
            }
            else {
                root2.addChild(root1);
                root1.setParent(root2);
                root2.upRank();
            }
        }
    }
    //Kruskal's MST search algorithm
    public static void kruskals(int n, int m, int[][] A) {
        
     // sort edges according to weights
        java.util.Arrays.sort(A, new java.util.Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return Integer.compare(a[2], b[2]);
            }
        });
        
        // add enumerations
        for (int i = 0; i < m; i++) {
            int[] a;
            a = A[i];
            a[3] = i;
            A[i] = a;   
//            System.out.print(a[3]);
        }
        
        // initialize all nodes array & other variables
        long totalCost = 0;
        int maxWeight = -1;
        int maxWeightPos = -1;
        int k = (int) Math.floor(n/2);
        int kDepth = 0;
        
        
        ArrayList<Node<Integer>> allNodes = new ArrayList<Node<Integer>>();
        
        // create leaf node for each vertex & add to array of nodes
        for (int i = 0; i < n; i++) {
            Node<Integer> aNode = makeSet(i);
            allNodes.add(aNode);
        }
        
        // go through all edges in ascending order of weight & add to mst 
        for (int j = 0; j < m; j++) {
            int[] a = A[j];
            Node<Integer> aNode = allNodes.get(a[0]);
            Node<Integer> bNode = allNodes.get(a[1]);

            
            Node<Integer> root1 = find(aNode);
            Node<Integer> root2 = find(bNode);
            
            // check if roots are connected
            if (root1.getData() != root2.getData()) {
                union(root1, root2);
                
                // update variables
                int w = a[2];
                int pos = a[3];
                if (w > maxWeight) {
                    maxWeight = w;
                    maxWeightPos = pos;
                }
                totalCost = totalCost + Long.valueOf(w);
            }
        }
        // check for kth element
        kDepth = getDepth(allNodes.get(k));
        
        System.out.println(totalCost);
        System.out.println(maxWeight);
        System.out.println(maxWeightPos + 1);
        System.out.println(kDepth);

    }
    static int n; //number of nodes
    static int m; //number of edges
  
    public static void main(String[] args) throws IOException {
        
        //read input line by line
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));        
        String line = stdin.readLine().trim();
        String[] l = line.split(" ");
        n = Integer.parseInt(l[0]);
        m = Integer.parseInt(l[1]);
        int[][] A = new int[m][4];
        int counter = 0;
        

        while ((line = stdin.readLine()) != null && line.length()!= 0) {
            String[] input = line.split(" ");
            for(int k = 0; k < input.length; k++) {
                    A[counter][k] = Integer.parseInt(input[k]);
            }
            counter++;
            
        }
        
        kruskals(n, m, A);
    }
}
