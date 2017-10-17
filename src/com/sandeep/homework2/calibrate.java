package com.sandeep.homework2;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class calibrate {
	private List<String> benchMarks;

	public static void main(String[] args) {

		calibrate calibrare = new calibrate();
		try {
			calibrate.runWithTimeout(new Runnable() {
				@Override
				public void run() {
					calibrare.getBenchMarkData();
				}
			}, 290000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			calibrare.writeBenchmarks();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> getBenchMarkData() {

		List<String> inputString = getInputList();
		benchMarks = new ArrayList<>();

		for (String input : inputString) {
			int processedDepth = 1;
			Args args = constructArgs(input);
			homework homework = new homework(args.getSizeOfBoard(), args.getNumOffruits(), args.getTimeTaken(),
					args.getBoard());
			while (processedDepth < 5) {
				Long startTime = System.currentTimeMillis();
				homework.runAlphaBetaPruning(processedDepth);
				Long end = (System.currentTimeMillis() - startTime) / 1000 + 1;
				String result = args.getSizeOfBoard() + "," + args.getNumOffruits() + ","
						+ Integer.toString(processedDepth) + "," + end.toString();
				System.out.println(result);
				benchMarks.add(result);
				processedDepth++;
			}

		}
		return benchMarks;
	}

	private void writeBenchmarks() throws IOException {
		Path file = Paths.get("calibration.txt");
		Files.write(file, benchMarks, Charset.forName("UTF-8"));
	}

	private Args constructArgs(String input) {

		List<String> inputData;
		Args args = new Args();
		inputData = new ArrayList<>(Arrays.asList(input.split("\n")));

		args.setSizeOfBoard(Integer.parseInt(inputData.remove(0)));
		args.setNumOffruits(Integer.parseInt(inputData.remove(0)));
		args.setTimeTaken(Float.parseFloat(inputData.remove(0)));
		char[][] board = new char[args.getSizeOfBoard()][args.getSizeOfBoard()];

		String inputString = null;
		for (int i = 0; i < args.getSizeOfBoard(); i++) {
			inputString = inputData.get(i);
			for (int j = 0; j < args.getSizeOfBoard(); j++) {
				board[i][j] = inputString.charAt(j);
			}
		}
		args.setBoard(board);
		return args;
	}

	private List<String> getInputList() {

		ArrayList<String> inputList = new ArrayList<>(Arrays.asList(
				"10\n3\n300.0\n1010220020\n0110120011\n0220000021\n1021222012\n0022222110\n1200022102\n0220201200\n1200122220\n0100222222\n0010010012",
				"10\n6\n300.0\n3454545141\n3555332554\n3212002514\n5400523532\n4515145121\n1442221513\n2135014054\n5254443223\n2531352035\n2055404035",
				"10\n10\n300.0\n9461649139\n5170024302\n1707286152\n6338059238\n7562660776\n3484845266\n8483203991\n5733722886\n2393919591\n3882997206",
				"15\n3\n300.0\n112000010000102\n110110121001122\n010011222222022\n101211121221122\n010001201101222\n122212121121122\n111010210012011\n022200220120102\n222211220021110\n220212021212020\n022020001210210\n022012122011002\n212110121121211\n020211021101010\n022002011012022",
				"15\n6\n300.0\n522021135455135\n304514553450455\n150225421500222\n033020114130135\n510234524200230\n521413321510051\n252241323244223\n310451020154040\n301515544320011\n320200012335414\n211342501521453\n425050500221402\n044320540453003\n005415414110020\n214005050222042",
				"15\n10\n300.0\n224708366505171\n251515553292692\n968256868682101\n613757227903911\n139470217981255\n648391825053481\n129952484503253\n107418451388747\n785314786738527\n176512830656963\n242527768589462\n821559477163550\n010401523998857\n661535360859186\n611999013480995",
				"20\n3\n300.0\n02222120022211202222\n12122222111012012001\n12022122100010002202\n22002101202000222022\n12001112202101122211\n22211121012120002012\n21020110120011012111\n21001200102202012002\n20012122002112102110\n12222002221100012221\n22221201021020212012\n02120112221001211110\n20020002111202012002\n12102201011211022202\n10200201020101011112\n11220012101112100122\n21220212121202022212\n20110111112020210202\n12111000021012221101\n22011110211010202102",
				"20\n6\n300.0\n50134314232244145434\n42542355350540102414\n41052100410032105431\n12144210240132315034\n03012033503454250104\n50410230230421531151\n43511103115400055114\n22143141102045220215\n43541525435520550420\n32204451533431051143\n50422300123352340421\n05233530530032344101\n40024340032303423415\n40111314011433111314\n41404011300533304130\n04001125554502305154\n30144343425055551435\n53143441315312431100\n42002001240230254140\n33103453323411113451",
				"20\n10\n300.0\n41033096914646207830\n60621979928082110798\n33379107832277663570\n16079123626133166457\n19381565272174409308\n56894322200181580474\n79102033227995117506\n54356047347392162359\n81915150792142482711\n69616773772967749182\n71729619141238149892\n18136026233807066896\n69254627372829839578\n33688055836397529577\n12725175283137946156\n98489419439829026287\n37666330625783677395\n19088808324979555100\n43170467435996650081\n20719795071260400281",
				"26\n3\n300.0\n12221112112021122120012100\n01002212102202020010021221\n10211120001000120221022101\n01210222101021000012000202\n11200220110020001211010121\n22210102200022122102120112\n11022011220102221012010011\n22222120000020100112012001\n02212101000011111210222100\n10202120102010101111111110\n02122211211001222201111110\n20220222212022121012022201\n12112212010022121121202110\n02111021021002010221101220\n01021122100210100201112001\n10202001210210000222011001\n10002011220200020221202222\n10022122002121022121001020\n20101000202012200021210212\n01221122202020220202121212\n20111220011220110110102001\n02000221012222010221210221\n01121210202010102210020122\n02002222200012121202222102\n22200210020211122000110122\n22211021021112000202120011\n",
				"26\n6\n300.0\n02153131534421504505533504\n33533210305103452044351222\n23024403315201334504154345\n33243421230142530311150054\n15545522231135040522451115\n14250114414352502404130121\n25022113341110353351404225\n05155311145315324515333035\n51311442224120305215400150\n04111543320452541341502454\n30112131122122302043251243\n44434113335043144344441455\n51254252120142122342020223\n25115154232134214025005133\n10450402104333531311022123\n05323143103221413552455143\n34355355400252345141250342\n32553435104140442015530515\n51230555015243535433445215\n15121024111121035133432545\n12120525535543224155233151\n43254455434251405424221321\n44432552231305001454033443\n43332255033135130230553013\n01300552411332234323513051\n33230544351534225510441302",
				"26\n10\n300.0\n72946317132000222735401161\n53358333260727208824810753\n32676772525463843931470796\n33454063941984469794017893\n11751137461819274315144611\n70909939567448101415704613\n45430556071291310940721767\n85571020798659176070458511\n42412844423364931965862333\n97017485249210492640139614\n31853633501995276676442830\n77111370560591164063578438\n23520032088949728696175648\n42249622761104307105543742\n20215887674837631132259275\n08469897621591166756383268\n86348482175780382346000461\n67690223052435811791990642\n14956086061140438643591122\n85408214913564006164272720\n11610886377932167757581353\n56547418549779679771200836\n17161843432957583341161544\n26157294348700181334747932\n87589670129506170451763054\n25624655562367903402971760"));
		return inputList;
	}

	public static void runWithTimeout(final Runnable runnable, long timeout, TimeUnit timeUnit) throws Exception {
		runWithTimeout(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				runnable.run();
				return null;
			}
		}, timeout, timeUnit);
	}

	public static <T> T runWithTimeout(Callable<T> callable, long timeout, TimeUnit timeUnit) throws Exception {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<T> future = executor.submit(callable);
		executor.shutdown();
		try {
			return future.get(timeout, timeUnit);
		} catch (TimeoutException e) {

			future.cancel(true);
			throw e;
		} catch (ExecutionException e) {
			Throwable t = e.getCause();
			if (t instanceof Error) {
				throw (Error) t;
			} else if (t instanceof Exception) {
				throw (Exception) t;
			} else {
				throw new IllegalStateException(t);
			}
		}
	}

	class Args {

		private int sizeOfBoard;
		private int numOffruits;
		private float timeTaken;
		private char[][] board;

		public Args(int sizeOfBoard, int numOffruits, float timeTaken, char[][] board) {
			super();
			this.sizeOfBoard = sizeOfBoard;
			this.numOffruits = numOffruits;
			this.timeTaken = timeTaken;
			this.board = board;
		}

		public Args() {

		}

		public int getSizeOfBoard() {
			return sizeOfBoard;
		}

		public void setSizeOfBoard(int sizeOfBoard) {
			this.sizeOfBoard = sizeOfBoard;
		}

		public int getNumOffruits() {
			return numOffruits;
		}

		public void setNumOffruits(int numOffruits) {
			this.numOffruits = numOffruits;
		}

		public float getTimeTaken() {
			return timeTaken;
		}

		public void setTimeTaken(float timeTaken) {
			this.timeTaken = timeTaken;
		}

		public char[][] getBoard() {
			return board;
		}

		public void setBoard(char[][] board) {
			this.board = board;
		}

	}

}
