package rubikscube;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class RubiksCube {
    private static final int SIZE = 3;
    private char[][][] cube;
    private static final int U = 0; // (Orange)
    private static final int L = 1; // (Green)
    private static final int F = 2; // (White)
    private static final int R = 3; // (Blue)
    private static final int B = 4; // (Yellow)
    private static final int D = 5; // (Red)
    
    private static final char[] COLORS = {'O', 'G', 'W', 'B', 'Y', 'R'};

    /**
     * default constructor
     */
    public RubiksCube() {
        cube = new char[6][SIZE][SIZE];
        for (int face = 0; face < 6; face++) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    cube[face][i][j] = COLORS[face];
                }
            }
        }
    }

    /**
     * Copy constructor
     */
    public RubiksCube(RubiksCube other) {
        this.cube = new char[6][SIZE][SIZE];
        for (int face = 0; face < 6; face++) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    this.cube[face][i][j] = other.cube[face][i][j];
                }
            }
        }
    }

    private void parse(String[] lines) throws IncorrectFormatException {
        // up
        for (int i = 0; i < 3; i++) {
            String line = lines[i].trim();
            if (line.length() != 3) {
                throw new IncorrectFormatException("INVALID");
            }
            for (int j = 0; j < SIZE; j++) {
                cube[U][i][j] = line.charAt(j);
            }
        }
        
        // middle (L, F, R, B)
        for (int i = 3; i < 6; i++) {
            String line = lines[i].trim();
            if (line.length() != 12) {
                throw new IncorrectFormatException("INVALID");
            }
            for (int j = 0; j < SIZE; j++) {
                cube[L][i-3][j] = line.charAt(j);
            }
            for (int j = 0; j < SIZE; j++) {
                cube[F][i-3][j] = line.charAt(j + 3);
            }
            for (int j = 0; j < SIZE; j++) {
                cube[R][i-3][j] = line.charAt(j + 6);
            }
            for (int j = 0; j < SIZE; j++) {
                cube[B][i-3][j] = line.charAt(j + 9);
            }
        }
        
        // down
        for (int i = 6; i < 9; i++) {
            String line = lines[i].trim();
            if (line.length() != 3) {
                throw new IncorrectFormatException("INVALID");
            }
            for (int j = 0; j < SIZE; j++) {
                cube[D][i-6][j] = line.charAt(j);
            }
        }
    }

    /**
     * Creates a Rubik's Cube from the description in fileName
     */
    public RubiksCube(String fileName) throws IOException, IncorrectFormatException {
        cube = new char[6][SIZE][SIZE];
        String[] lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            lines = new String[9];
            for (int i = 0; i < 9; i++) {
                lines[i] = reader.readLine();
                if (lines[i] == null) {
                    throw new IncorrectFormatException("INVALID");
                }
            }
        } catch (IOException e) {
            throw new IOException("N/A");
        }
        
        parse(lines);
    }

    private void rotateFace(int face) {
        char[][] newFace = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                newFace[j][SIZE-1-i] = cube[face][i][j];
            }
        }
        cube[face] = newFace;
    }

    private void moveF() {
        rotateFace(F);
        char[] temp = Arrays.copyOf(cube[U][2], SIZE);
        
        for (int i = 0; i < SIZE; i++) {
            cube[U][2][i] = cube[L][2-i][2];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[L][i][2] = cube[D][0][i];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[D][0][i] = cube[R][2-i][0];
        }

        for (int i = 0; i < SIZE; i++) {
            cube[R][i][0] = temp[i];
        }
    }

    private void moveL() {
        rotateFace(L);
        char[] temp = new char[SIZE];
        for (int i = 0; i < SIZE; i++) {
            temp[i] = cube[U][i][0];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[U][i][0] = cube[B][2-i][2];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[B][i][2] = cube[D][2-i][0];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[D][i][0] = cube[F][i][0];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[F][i][0] = temp[i];
        }
    }
    
    private void moveR() {
        rotateFace(R);
        char[] temp = new char[SIZE];
        for (int i = 0; i < SIZE; i++) {
            temp[i] = cube[U][i][2];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[U][i][2] = cube[F][i][2];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[F][i][2] = cube[D][i][2];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[D][i][2] = cube[B][2-i][0];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[B][i][0] = temp[2-i];
        }
    }
    
    private void moveU() {
        rotateFace(U);
        char[] temp = Arrays.copyOf(cube[F][0], SIZE);
        
        for (int i = 0; i < SIZE; i++) {
            cube[F][0][i] = cube[R][0][i];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[R][0][i] = cube[B][0][i];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[B][0][i] = cube[L][0][i];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[L][0][i] = temp[i];
        }
    }
    
   private void moveD() {
        rotateFace(D);
        char[] temp = Arrays.copyOf(cube[F][2], SIZE);

        for (int i = 0; i < SIZE; i++) {
            cube[F][2][i] = cube[L][2][i];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[L][2][i] = cube[B][2][i];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[B][2][i] = cube[R][2][i];
        }
        
        for (int i = 0; i < SIZE; i++) {
            cube[R][2][i] = temp[i];
        }
    }
    
    private void moveB() {
        rotateFace(B);
        char[] temp = Arrays.copyOf(cube[U][0], SIZE);
    
        for (int i = 0; i < SIZE; i++) {
        cube[U][0][i] = cube[L][i][0];
        }
    
        for (int i = 0; i < SIZE; i++) {
            cube[L][i][0] = cube[D][2][i];
        }
    
        for (int i = 0; i < SIZE; i++) {
            cube[D][2][i] = cube[R][2-i][2];
        }
    
        for (int i = 0; i < SIZE; i++) {
            cube[R][i][2] = temp[2-i];
        }
}

    /**
     * Applies the sequence of moves on the Rubik's Cube
     */
    public void applyMoves(String moves) {
        for (char move : moves.toCharArray()) {
            if (move == 'F') {
                moveF();
            } else if (move == 'L') {
                moveL();
            } else if (move == 'R') {
                moveR();
            } else if (move == 'U') {
                moveU();
            } else if (move == 'D') {
                moveD();
            } else if (move == 'B') {
                moveB();
            }
        }
    }

    /**
     * returns true if the current state of the Cube is solved
     */
    public boolean isSolved() {
        for (int face = 0; face < 6; face++) {
            char expectedColor = COLORS[face];
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (cube[face][i][j] != expectedColor) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Up 
        for (int i = 0; i < SIZE; i++) {
            sb.append("   ");
            for (int j = 0; j < SIZE; j++) {
                sb.append(cube[U][i][j]);
            }
            sb.append("\n");
        }
        
        // Middle 
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(cube[L][i][j]);
            }
            for (int j = 0; j < SIZE; j++) {
                sb.append(cube[F][i][j]);
            }
            for (int j = 0; j < SIZE; j++) {
                sb.append(cube[R][i][j]);
            }
            for (int j = 0; j < SIZE; j++) {
                sb.append(cube[B][i][j]);
            }
            sb.append("\n");
        }
        
        // Down 
        for (int i = 0; i < SIZE; i++) {
            sb.append("   ");
            for (int j = 0; j < SIZE; j++) {
                sb.append(cube[D][i][j]);
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RubiksCube other = (RubiksCube) obj;
        return Arrays.deepEquals(this.cube, other.cube);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(cube);
    }

    /**
     * @param moves
     * @return the order of the sequence of moves
     */
    public static int order(String moves) {
        RubiksCube cube = new RubiksCube();
        RubiksCube initial = new RubiksCube();
    
        int count = 0;
        while (true) {
            cube.applyMoves(moves);
            count++;
            if (cube.equals(initial)) {
                return count;
            }
        }
    }

    public static char getColor(int face) {
    return COLORS[face];


}


/**
 * Gets the color of a specific facelet
 */
public char getFacelet(int face, int i, int j) {
    return cube[face][i][j];
}

public int getHeuristicValue() {
    int misplaced = 0;
    for (int face = 0; face < 6; face++) {
        char expectedColor = COLORS[face];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (cube[face][i][j] != expectedColor) {
                    misplaced++;
                }
            }
        }
    }
    return (misplaced + 7) / 8; 
}
}