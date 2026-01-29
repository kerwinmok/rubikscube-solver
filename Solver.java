package rubikscube;
import java.io.*;

public class Solver {
    // All moves including inverses
    private static final String[] MOVES = {"F", "L", "R", "U", "D", "B", 
                                           "FF", "LL", "RR", "UU", "DD", "BB",
                                           "FFF", "LLL", "RRR", "UUU", "DDD", "BBB"};
    private static final int MAX_DEPTH = 21; 
    private static String solution = null;
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("not specified");
            System.out.println("usage: java rubikscube.Solver input_file output_file");
            return;
        }
        
        try {
            RubiksCube cube = new RubiksCube(args[0]);
            
            String solution = solveIDAStar(cube);
            
            if (solution != null) {
                writeSolution(args[1], solution);
                System.out.println("Solution: " + solution);
                System.out.println("Length: " + solution.length() + " moves"); //just length estimate, counts FFF as 3 moves and not one amd FF and 2, etc (NOT ACCURATE)
            } else {
                writeSolution(args[1], "");
                System.out.println("No solution " + MAX_DEPTH);
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String solveIDAStar(RubiksCube start) {
        solution = null;
        int startDepth = heuristic(start); 
         
        for (int depth = startDepth; depth <= MAX_DEPTH && solution == null; depth++) {
            System.out.println("depth: " + depth);
            depthLimitedSearch(start, depth, "", -1);
            if (solution != null) {
                return solution;
            }
        }
        return null;
    }
    
    private static void depthLimitedSearch(RubiksCube cube, int depth, String path, int last) {
        if (solution != null) return;
        
        if (cube.isSolved()) {
            solution = path;
            return;
        }
        
        if (depth == 0) return;
        
        int h = heuristic(cube);
        if (h > depth) {
            return;
        }
        
        for (int i = 0; i < MOVES.length && solution == null; i++) {
            String move = MOVES[i];
            char moveFace = move.charAt(0);
            
            if (last >= 0) {
                String lastStr = MOVES[last];
                char lastMoveFace = lastStr.charAt(0);
                if (moveFace == lastMoveFace) {
                    continue;
                }
            }
            
            if (last >= 0 && path.length() > 0) {
                String lastStr = MOVES[last];
                
                if (areInverseMoves(lastStr, move)) {
                    continue;
                }
                
                if ((lastStr.length() == 1 && move.length() == 2 && lastStr.charAt(0) == moveFace) ||
                    (lastStr.length() == 2 && move.length() == 1 && lastStr.charAt(0) == moveFace)) {
                    continue;
                }
            }
            
            RubiksCube next = new RubiksCube(cube);
            next.applyMoves(move);
            
            depthLimitedSearch(next, depth - 1, path + move, i);
        }
    }
    
    private static boolean areInverseMoves(String x, String y) {
        if (x.charAt(0) != y.charAt(0)) return false;
        
        int len1 = x.length();
        int len2 = y.length();
        
        return (len1 == 1 && len2 == 3) || 
               (len1 == 3 && len2 == 1) ||
               (len1 == 2 && len2 == 2);
    }
    
    private static int heuristic(RubiksCube cube) {
        int stickerLowerBound = cube.getHeuristicValue();

        int cornerLowerBound = cornerManhattan(cube);
        int edgeLowerBound   = edgeManhattan(cube);

        return Math.max(stickerLowerBound, Math.max(cornerLowerBound, edgeLowerBound));
    }
    
    private static int cornerManhattan(RubiksCube cube) {
        int totalDistance = 0;
        
        int[][][] corners = {
            {{0,0,0}, {1,0,2}, {4,0,0}}, // ULB
            {{0,0,2}, {3,0,0}, {4,0,2}}, // URB  
            {{0,2,0}, {1,2,2}, {2,0,0}}, // ULF
            {{0,2,2}, {2,0,2}, {3,0,2}}, // URF
            {{5,0,0}, {1,0,0}, {2,2,0}}, // DLF
            {{5,0,2}, {2,2,2}, {3,2,0}}, // DRF
            {{5,2,0}, {1,2,0}, {4,2,2}}, // DLB
            {{5,2,2}, {3,2,2}, {4,2,0}}  // DRB
        };
        
        for (int c = 0; c < 8; c++) {
            int[][] corner = corners[c];
            
            char color1 = cube.getFacelet(corner[0][0], corner[0][1], corner[0][2]);
            char color2 = cube.getFacelet(corner[1][0], corner[1][1], corner[1][2]);
            char color3 = cube.getFacelet(corner[2][0], corner[2][1], corner[2][2]);
            
            int minDistance = 3; 
            
            for (int targetCorner = 0; targetCorner < 8; targetCorner++) {
                int[][] target = corners[targetCorner];
                char targetColor1 = RubiksCube.getColor(target[0][0]);
                char targetColor2 = RubiksCube.getColor(target[1][0]);
                char targetColor3 = RubiksCube.getColor(target[2][0]);
                
                boolean matches = (color1 == targetColor1 || color1 == targetColor2 || color1 == targetColor3) &&
                                 (color2 == targetColor1 || color2 == targetColor2 || color2 == targetColor3) &&
                                 (color3 == targetColor1 || color3 == targetColor2 || color3 == targetColor3);
                
                if (matches) {
                    int distance;
                    if (c == targetCorner) {
                        distance = 0;
                    } 
                    else {
                        distance = 1;
                    }
                    minDistance = Math.min(minDistance, distance);
                }
            }
            
            totalDistance += minDistance;
        }
        
        return (totalDistance + 3) / 4;
    }
    
    private static int edgeManhattan(RubiksCube cube) {
        int totalDistance = 0;
        
        int[][][] edges = {
            {{0,0,1}, {4,0,1}}, // UB
            {{0,1,0}, {1,1,2}}, // UL
            {{0,1,2}, {3,1,0}}, // UR
            {{0,2,1}, {2,0,1}}, // UF
            {{5,0,1}, {2,2,1}}, // DF
            {{5,1,0}, {1,1,0}}, // DL
            {{5,1,2}, {3,1,2}}, // DR
            {{5,2,1}, {4,2,1}}, // DB
            {{1,0,1}, {4,1,2}}, // LB
            {{1,2,1}, {2,1,0}}, // LF
            {{3,0,1}, {4,1,0}}, // RB
            {{3,2,1}, {2,1,2}}  // RF
        };
        
        for (int e = 0; e < 12; e++) {
            int[][] edge = edges[e];
            
            char color1 = cube.getFacelet(edge[0][0], edge[0][1], edge[0][2]);
            char color2 = cube.getFacelet(edge[1][0], edge[1][1], edge[1][2]);
            
            int minDistance = 2; 
            
            for (int targetEdge = 0; targetEdge < 12; targetEdge++) {
                int[][] target = edges[targetEdge];
                char targetColor1 = RubiksCube.getColor(target[0][0]);
                char targetColor2 = RubiksCube.getColor(target[1][0]);
                
                boolean matches = (color1 == targetColor1 || color1 == targetColor2) &&
                                 (color2 == targetColor1 || color2 == targetColor2);
                
                if (matches) {
                    int distance = (e == targetEdge) ? 0 : 1;
                    minDistance = Math.min(minDistance, distance);
                }
            }
            
            totalDistance += minDistance;
        }
        
        return (totalDistance + 2) / 3;
    }
    


    
    private static void writeSolution(String filename, String solution) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.print(solution);
        } catch (IOException e) {
            System.err.println("Error:" + e.getMessage());
        }
    }
}
