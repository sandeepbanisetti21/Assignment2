package com.sandeep.homework2;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class Point{
    Integer x, y;
    public Point(Integer x, Integer y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString(){
        return "(" + x.toString() + ", " + y.toString() + ") ";
    }
}

class PointScore{
    Integer score;
    Point point;
    public PointScore(Integer score, Point point){
        this.score = score; // this in a state will be a difference in scores of min and max player
        this.point = point;
    }

    public static PointScore max(PointScore p1, PointScore p2){
        if(p1 == null)
            return p2;
        if(p2 == null)
            return p1;
        if(p1.score >= p2.score)
            return p1;
        return p2;
    }

    public static PointScore min(PointScore p1, PointScore p2){
        if(p1 == null)
            return p2;
        if(p2 == null)
            return p1;

        if(p1.score < p2.score)
            return p1;
        return p2;
    }

    @Override
    public String toString(){
        if(point == null && score == null)
            return "";
        if(point == null)
            return "score: " + score.toString() + " ()";
        if(score == null)
            return "( " + point.toString() + " )";
        return "score: " + score.toString() + " " + point.toString();
    }
}

class Input{
    Integer [][] board;
    Integer size, fruits;
    Double time;
    public Input(Integer [][] board, Integer size, Integer fruits, Double time){
        this.board = board;
        this.size = size;
        this.fruits = fruits;
        this.time = time;
    }
}

public class FruitSolver {

    static Integer emptyFruit = -1;
    static Integer children_size_threshold = 5;


    public Integer fruitSlots(Integer [][] board, Integer size){
        Integer i, j, c=0;
        for(i=0;i<size;i++)
            for(j=0;j<size;j++)
                if(board[i][j] != emptyFruit)
                    c++;
        return c;
    }


    public Integer getBenchmark(Integer size, Integer fruits, Double time, ArrayList<ArrayList<Integer>> information){
        Integer infoSize = information.size(), i = 0, j, k;
        //ToDo: check if you change information order, (size, fruits, depth, time)

        while(i<infoSize && size > information.get(i).get(0))
            i++;

        //move down till we find proper benchmark depth with size locked
        while(i<infoSize && fruits > information.get(i).get(1))
            i++;

        j=4*((i/4)+1);
        k=i;
        while(i<infoSize && i<j && time >= information.get(i).get(3))
            i++;

        if(i>k) // check if we moved ahead
            i--;

        if(i>=0 && i<infoSize)
            return information.get(i).get(2);
        else
            return 2;
    }


    public Integer getDepth(Input input){
        if(input.time < 2)
            return 0; // random move

        if(input.time < 5)
            return 1;

        if(input.time < 10 && input.size >= 20)
            return 1;

        if(input.time > 280 && input.size > 20)
            return 3;

        if(input.time >= 100 && input.size <= 8)
            return 6;

        if(input.time >= 150 && input.size < 13)
            return 5;

        if(input.time > 250 && input.size > 13 && input.size <= 20)
            return 4;

        if(input.time > 150 && input.size <= 13)
            return 3;

        if(input.time >= 30 && input.size <= 10)
            return 4;


        ArrayList<ArrayList<Integer>> information = readBenchmarks();

        Integer boardFruits = fruitSlots(input.board, input.size);
        Integer distinctFruits = fruitCount(input.board, input.size);
        Integer remStpsLowBound = boardFruits/distinctFruits;

        if(input.time > 200 && input.size < 20 && remStpsLowBound < 10)
            return 4;

        if(input.time > 50 && boardFruits < 100)
            return 4;

        if(input.time < 10 && remStpsLowBound >= 25)
            return 1;

        if(input.time < 10 && remStpsLowBound < 10)
            return 2;

        //ToDo: the 1.3 ratio is a rough bound, experiment and change this
        Double perStepTime = input.time/(1.4*remStpsLowBound);

        if(perStepTime < 0.1 && input.time < 10)
            return 0; //less then 0.1 sec per step, may need random now.

        if(perStepTime < 1)
            perStepTime = 1.0; // rounding off to 1 sec

        return getBenchmark(input.size, input.fruits, perStepTime, information);
    }

    public void printBoard(Integer [][] board, Integer size){
        Integer i, j;
        for(i=0;i<size;i++){
            for(j=0;j<size;j++){
                if(board[i][j] == -1)
                    System.out.print("*");
                else
                    System.out.print(board[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public Input readInput(){
        Stream<String> rawData;
        List<String> data;
        String cur, val;
        Integer size, fruits, j, i;
        Integer [][] board;
        Double time;
        try{
            String curDir = System.getProperty("user.dir");
            rawData = Files.lines(Paths.get(curDir + "/input.txt"));
            data = rawData.collect(Collectors.toList());
            size = Integer.parseInt(data.remove(0));
            fruits = Integer.parseInt(data.remove(0));
            time = Double.parseDouble(data.remove(0));
            board = new Integer[size][size];
            for(i = 0; i<size; i++){
                cur = data.remove(0);
                for(j=0; j<size; j++){
                    val = String.valueOf(cur.charAt(j));
                    if(val.equals("*"))
                        board[i][j] = -1;
                    else{
                        board[i][j] = Integer.parseInt(val);
                    }
                }
            }
            return new Input(board, size, fruits, time);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void writeOutput(Point p, Integer [][] board, Integer size) throws IOException {
        removeFruit(board, p, size);
        p.x++; //check: to convert this to 1 based index
        String firstLine = String.valueOf((char)((int)'A' + p.y)) + p.x.toString() + "\n";
        String curDir = System.getProperty("user.dir");
        FileWriter writer = new FileWriter(curDir + "/output.txt");
//        System.out.println(firstLine);
        Integer i, j;
        StringBuilder boardString = new StringBuilder(firstLine);
        for(i=0;i<size;i++){
            StringBuilder cur = new StringBuilder("");
            for(j=0;j<size;j++){
                if(board[i][j] == -1)
                    cur.append("*");
                else
                    cur.append(String.valueOf(board[i][j]));
            }
            cur.append("\n");
            boardString.append(cur);
        }
        boardString.setLength(boardString.length() - 1);
        writer.write(boardString.toString());
        writer.close();
    }

    public ArrayList<ArrayList<Integer>> readBenchmarks(){
        ArrayList<ArrayList<Integer>> information = new ArrayList<>();
        Iterator<String> data;
        String str, curDir = System.getProperty("user.dir");
        try{
            data = Files.lines(Paths.get(curDir + "/calibration.txt")).collect(Collectors.toList()).iterator();
            while(data.hasNext()){
                str=data.next();
                ArrayList<Integer> tmp = new ArrayList<>();
                for(String s: Arrays.asList(str.split(","))){
                    tmp.add(Integer.parseInt(s));
                }
                information.add(tmp);
            }
//            System.out.println(information.size());
            return information;
        } catch (IOException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // has side effect, makes changes into the data structure passed
    public void removeFruitRecr(Integer [][] board, Point point, Integer val, Integer size){
        Integer x = point.x, y = point.y;
        if(x >= 0 && x < size && y >= 0 && y < size && board[x][y] != -1) {
            if (board[x][y] == val) {
                board[x][y] = -1; // this represents that fruit has been claimed
                removeFruitRecr(board, new Point(x, y+1) , val, size);
                removeFruitRecr(board, new Point(x, y-1) , val, size);
                removeFruitRecr(board, new Point(x+1, y) , val, size);
                removeFruitRecr(board, new Point(x-1, y) , val, size);
            }
        }
    }

    // applies gravity, has side effect
    public void gravity(Integer [][] board, Integer size){
        Integer i, j, k=0;
        Integer [] valid = new Integer[size];
        for(j=0;j<size;j++){
            //for each row
            for(i=0;i<size;i++){
                if(board[i][j] != emptyFruit){
                    valid[k] = board[i][j];
                    k++;
                }
            }
            i=size-1;
            while(i>=0 && k>0){
                k--;
                board[i][j] = valid[k];
                i--;
            }
            while(i>=0){
                board[i][j] = emptyFruit;
                i--;
            }
        }
    }

    // has side effect
    public void removeFruit(Integer [][] board, Point point, Integer size){
        removeFruitRecr(board, point, board[point.x][point.y], size);
        gravity(board, size);
    }

    public Integer [][] deepcopy(Integer [][] board, Integer size){
        Integer [][] newBoard = new Integer[size][size];
        Integer i, j;
        for(i=0;i<size;i++)
            for(j=0;j<size;j++)
                newBoard[i][j] = board[i][j];
        return newBoard;
    }

    //has no side effects on board
    public Integer pointValRecr(Integer [][] board, Integer [][] visited, Integer size, Point point, Integer val){
        Integer x = point.x, y = point.y, cur;
        if(x >= 0 && x < size && y >= 0 && y < size && visited[x][y] == 0 && board[x][y] == val){
            visited[x][y] = 1;
            cur =   pointValRecr(board, visited, size, new Point(x+1, y), val) +
                    pointValRecr(board, visited, size, new Point(x-1, y), val) +
                    pointValRecr(board, visited, size, new Point(x, y+1), val) +
                    pointValRecr(board, visited, size, new Point(x, y-1), val);
            return 1+cur;
        }
        else
            return 0;
    }

    private Boolean checkIfConnectedPoints(Integer [][] board, Integer [][] visited, Integer size, Point p1, Point p2){

//        System.out.println(p1);

        if(p1.x == p2.x && p1.y == p2.y)
            return true;

        Integer x = p1.x, y = p1.y;
        if(x >= 0 && x < size && y >= 0 && y < size && visited[p1.x][p1.y] == 0){
            if(board[p1.x][p1.y] != board[p2.x][p2.y])
                return false;

            visited[x][y] = 1;

            return  checkIfConnectedPoints(board, visited, size, new Point(x+1, y), p2) ||
                    checkIfConnectedPoints(board, visited, size, new Point(x, y+1), p2) ||
                    checkIfConnectedPoints(board, visited, size, new Point(x-1, y), p2) ||
                    checkIfConnectedPoints(board, visited, size, new Point(x, y-1), p2);
        }
        return false;
    }

    public Boolean checkIfConnected(Integer [][] board, Integer size, Point p1, Point p2){
        Integer [][] visited = zeroBoard(size);
        return checkIfConnectedPoints(board, visited, size, p1, p2);
    }

    public Integer fruitCount(Integer [][] board, Integer size){
        Integer i, j, c=0;
        Integer [] visited = new Integer[10];

        for(i=0;i<10;i++)
            visited[i] = 0;

        for(i=0;i<size;i++)
            for(j=0;j<size;j++)
                if(board[i][j] != -1)
                    visited[board[i][j]] = 1;

        for(i=0;i<10;i++)
            if(visited[i] != 0)
                c++;
        return c;
    }

    public Integer [][] zeroBoard(Integer size){
        Integer [][] visited = new Integer[size][size];
        Integer i, j;
        for(i=0;i<size;i++)
            for(j=0;j<size;j++)
                visited[i][j] = 0;
        return visited;
    }

    public boolean checkIfEnd(Integer [][] board, Integer size){
        Integer i, j;
        for(i=size-1;i>=0;i--){
            for(j=size-1;j>=0;j--){
                if(board[i][j] != -1){
                    return true;
                }
            }
        }
        return false;
    }

//    public ArrayList<Point> sortPoints(ArrayList<Point> points){
//        points.sort(new Comparator<Point>() {
//            @Override
//            public int compare(Point point, Point t1) {
//                return point.x.compareTo(t1.x)
//            }
//        });
//        return points;
//    }

    //has no side effects on board
    public Integer pointVal(Integer [][] board, Integer size, Point point){
        Integer [][] visited = zeroBoard(size);
        Integer f;
        if(board[point.x][point.y] == -1)
            return 0;
        f = pointValRecr(board, visited, size, point, board[point.x][point.y]);
        return f*f;
    }

    public Integer utility(Integer [][] board, Integer size, Boolean maxTurn) {
        Integer[][] visited = new Integer[size][size];
        Integer i, j, max1C=0, max2C=0, current;

        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++)
                visited[i][j] = 0;

        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                if(board[i][j] != emptyFruit){
                    current = pointValRecr(board, visited, size, new Point(i, j), board[i][j]);
                    if (current > max1C) {
                        max2C = max1C;
                        max1C = current;
                    } else if (current <= max1C && current > max2C)
                        max2C = current;
                }
            }
        }

        if (maxTurn) {
            return max1C * max1C - max2C * max2C;
        } else {
            return max2C * max2C - max1C * max1C;
        }
    }

    public ArrayList<Point> getChildren(Integer board[][], Integer size){

        Integer i, j, curCount, prev, l;
        ArrayList<Point> points = new ArrayList<>();

        // return all points if size is less then threshold
        if(size < children_size_threshold){
            for(i=0;i<size;i++)
                for(j=0;j<size;j++)
                    if(board[i][j] != -1)
                        points.add(new Point(i, j));
        }

        else{

            HashMap<Point, Integer> pointVal;
            //row wise max

            for(i=0;i<size;i++){
                j=1;
                prev = board[i][j-1];
                pointVal = new HashMap<>();
                curCount=1;
                while(j<size){
                    if(board[i][j] != prev){
                        if(board[i][j-1] != -1) // ensure -1 is not added
                            pointVal.put(new Point(i, j-1), curCount);
                        curCount = 0;
                        prev = board[i][j];
                    }
                    j++;
                    curCount++;
                }
                if(board[i][j-1] != -1) // ensure -1 is not added
                    pointVal.put(new Point(i, j-1), curCount);
                points.addAll(takeTop(pointVal));
            }

            //col wise max
            for(i=0;i<size;i++){
                j=1;
                prev = board[j-1][i];
                pointVal = new HashMap<>();
                curCount = 1;
                while (j<size){
                    if(board[j][i] != prev){
                        if(board[j-1][i] != -1) // ensure -1 is not added
                            pointVal.put(new Point(j-1, i), curCount);
                        curCount = 0;
                        prev = board[j][i];
                    }
                    j++;
                    curCount++;
                }
                if(board[j-1][i] != -1) // ensure -1 is not added
                    pointVal.put(new Point(j-1, i), curCount);
                points.addAll(takeTop(pointVal));
            }
        }
        return removeDuplicates(points, board, size);
    }

    public ArrayList<Point> removeDuplicates(ArrayList<Point> points, Integer [][] board, Integer size){
//        System.out.println(points);
        Integer i, j, l=points.size();
        Point p1, p2;
        ArrayList<Point> uniquePoints = new ArrayList<>();
        for(i=0;i<l;i++){
            Boolean con = true;
            for(j=i+1;j<l;j++){
                p1 = points.get(i);
                p2 = points.get(j);
                if(p1.x == p2.x && p1.y == p2.y){
                    con = false;
//                    System.out.println(p1.toString() +  p2.toString());
                    break;
                }
            }
            if(con)
                uniquePoints.add(points.get(i));
        }
        return uniquePoints;
    }

    //sorts and returns in descending order
    public ArrayList<Point> sortMap(HashMap<Point, Integer> map){
        List list = new LinkedList(map.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2){
                return ((Comparable)((Map.Entry)(o1)).getValue()).compareTo(((Map.Entry)(o2)).getValue());
            }
        });

        Collections.reverse(list);
        ArrayList<Point> points = new ArrayList<>();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            points.add((Point)entry.getKey());
        }
        return points;
    }

    public ArrayList<Point> takeTop(HashMap<Point, Integer> pointVal){
        ArrayList<Point> list = sortMap(pointVal);
        if(list.size() > 2)
            return new ArrayList<>(list.subList(0, 2)); //taking top 2
        else
            return list;
    }

    public boolean terminalCondition(Integer [][] board, Integer size, ArrayList<Point> children){
        Integer [][] newBoard = deepcopy(board, size);
        if(children.size() <= 1)
            return true;

        removeFruit(newBoard, children.get(0), size);
        if(!checkIfEnd(newBoard, size))
            return true;

        return false;
    }

    public PointScore greedyNode(Integer [][] board, Integer size, Integer fruits){
        Integer i, j, current, maxC=0, maxI, maxJ;
        PointScore fn = fastNode(board, size, fruits);
        maxI = fn.point.x;
        maxJ = fn.point.y;
        Integer [][] visited = new Integer[size][size];
        for(i=0;i<size;i++)
            for(j=0;j<size;j++)
                visited[i][j] = 0;
        for(i=0;i<size;i++)
        {
            for(j=0;j<size;j++){
                if(board[i][j] != emptyFruit){
                    current = pointValRecr(board, visited, size, new Point(i, j), board[i][j]);
                    if(current > maxC){
                        maxC = current;
                        maxI = i;
                        maxJ = j;
                    }
                }
            }
        }
        return new PointScore(maxC*maxC, new Point(maxI, maxJ));
    }

    //returns any non * node which it first encounters while searching from bottem
    public PointScore fastNode(Integer [][] board, Integer size, Integer fruits){
        Integer i, j;
        //check from back, since beginning will be usually *
        for(i=size-1;i>=0;i--)
            for(j=size-1;j>=0;j--)
                if(board[i][j] != -1)
                    return new PointScore(0, new Point(i,j));

        return null;
    }

    public PointScore minNode(Integer [][] board, Integer size, Integer fruits, Integer depth, Integer curScore, Integer alpha, Integer beta){
        if(depth == 0)
            return new PointScore(curScore+utility(board, size, false), null);

        ArrayList<Point> children = getChildren(board, size);
        Integer i, s, cCount = children.size(), bestScore = Integer.MAX_VALUE;
        PointScore v = new PointScore(Integer.MAX_VALUE, null);
        Integer [][] newBoard;
        Point point;

        if(terminalCondition(board, size, children)){
            if(children.size() == 0)
                return new PointScore(curScore, null);
            return new PointScore(curScore + utility(board, size, false), children.get(0));
        }

        for(i=0;i<cCount;i++){
            point = children.get(i);
            s = pointVal(board, size, point);
            newBoard = deepcopy(board, size);
            removeFruit(newBoard, point, size);

            v = PointScore.min(v, maxNode(newBoard, size, fruits, depth-1, curScore-s, alpha, beta));
            if(v.score < bestScore){
                bestScore = v.score;
                v.point = point;
            }

            if(v.score <= alpha){
//                System.out.println("pruned");
                return v;
            }
            beta = Integer.min(v.score, beta);
        }
        return v;
    }

    public PointScore maxNode(Integer [][] board, Integer size, Integer fruits, Integer depth, Integer curScore, Integer alpha, Integer beta){
        // terminal check may include no branches from here on
        if(depth == 0)
            return new PointScore(curScore+ utility(board,size, true), null);

        ArrayList<Point> children = getChildren(board, size);
        Integer i, s, cCount = children.size(), bestScore = Integer.MIN_VALUE;
        PointScore v = new PointScore(Integer.MIN_VALUE, null);
        Integer [][] newBoard;
        Point point;

//        System.out.println(children);
        if(terminalCondition(board, size, children)){
            if(children.size() == 0)
                return new PointScore(curScore, null);
            return new PointScore(curScore + utility(board, size, true), children.get(0));
        }

        for(i=0;i<cCount;i++){
            point = children.get(i);
            s = pointVal(board, size, point); // current move score
            newBoard = deepcopy(board, size);
            removeFruit(newBoard, point, size);

            // check if terminal condition is reached
            v = PointScore.max(v, minNode(newBoard, size, fruits, depth-1, curScore+s, alpha, beta));

            // to ensure the point is copied in case of yielding best score
            if(v.score > bestScore){
                bestScore = v.score;
                v.point = point;
            }

            if(v.score >= beta){
//                System.out.println("pruned");
                return v;
            }
            alpha = Integer.max(alpha, v.score);


        }
        return v;
    }

    public PointScore alpha_beta(Input input, Integer depth){
        Integer [][] newBoard = deepcopy(input.board, input.size);
        PointScore p = maxNode(input.board, input.size, input.fruits, depth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        p.score = pointVal(newBoard, input.size, p.point);
        return p;
    }


    public PointScore run(){
        Input input = readInput();
        Integer depth = getDepth(input);
        System.out.println("estimated depth: " + depth);
        PointScore ans = alpha_beta(input, depth);
        try{
            writeOutput(ans.point, input.board, input.size);
        } catch (IOException e){
            e.printStackTrace();
        }
        return ans;
    }

    //ToDo: add time check, return random in case of any unforeseen failures
    public static void main(String [] args){
        FruitSolver fs = new FruitSolver();
        Input input = fs.readInput();
        Integer depth = fs.getDepth(input);
        System.out.println("estimated depth: " + depth);
        Point ans;
        if(depth == 0){
            ans = fs.fastNode(input.board, input.size, input.size).point;
        }
        else{
            ans = fs.alpha_beta(input, depth).point;
        }

        try{
            fs.writeOutput(ans, input.board, input.size);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}

//ToDo: check for small test cases
//ToDo: discuss and implement iterative deepening
//ToDo: calibrate script
//ToDo: rule based mining for depth value
//ToDo: use slots/fruits ratio information
