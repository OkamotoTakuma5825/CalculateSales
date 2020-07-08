package jp.alhinc.okamoto_takuma.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CalculateSales {
	public static void main(String[] args) {
		HashMap<String, String> nameMap = new HashMap<>();
		HashMap<String, Long> amountMap = new HashMap<>();


		BufferedReader br = null;
		try {
			File file1 = new File(args[0], "branch.lst");

			//支店定義ファイルが存在しない場合
			if (!file1.exists()) {
				System.out.println("支店定義ファイルが存在しません");
				return;
			}

			FileReader fr1 = new FileReader(file1);
			br = new BufferedReader(fr1);
			String line;
			while((line = br.readLine()) != null) {
				String[] words = line.split(",",0); //文字列を分割

				//支店コード数字3桁
				if (!words[0].matches("^[0-9]{3}$")) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}

				//カンマ、改行を含まない文字
				if (words.length >= 3) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}

				nameMap.put(words[0],words[1]);
				amountMap.put(words[0],0L);
			}

		} catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch(IOException e) {
				}
			}
		}

		File file2 = new File(args[0]);
		File fileArray[] = file2.listFiles();

		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].getName().matches("^[0-9]{8}.rcd$")) {

//				rcdファイルのデータを配列に格納
				try {
					FileReader fr2 = new FileReader(fileArray[i]);
					br = new BufferedReader(fr2);
					ArrayList<String> array = new ArrayList<>(); //rcdファイルから読み込んだ内容を格納する
					String line; //rcdファイルを1行ずつ読み込む
					while((line = br.readLine()) != null) { //rcdファイルの行がある限り続行
						array.add(line); //読み込んだ文字をarrayに格納
					}

					// 支店に該当がなかった場合
					if (!nameMap.containsKey(array.get(0))) {
						System.out.println(fileArray[i].getName() + "の支店コードが不正です");
						return;
					}

					long j = amountMap.get(array.get(0));
					j += Long.parseLong(array.get(1));
					amountMap.put(array.get(0),j);

					// 合計金額10桁超え
					String sum = Long.toString(j);
					if (10 < sum.length()) {
						System.out.println("合計金額が10桁を超えました");
						return;
					}

					// 売上ファイルの内容が3行以上
					if(array.size() >= 3){
						System.out.println(fileArray[i].getName() + "のフォーマットが不正です");
						return;
					}
				} catch(IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				} finally {
					if(br != null) {
						try {
							br.close();
						} catch(IOException e) {
						}
					}
				}
			}

			try {
				File file3 = new File(args[0], "branch.out");
				FileWriter fw = new FileWriter(file3);
				BufferedWriter bw = new BufferedWriter(fw);

				for (String key : amountMap.keySet()) {
					bw.write(key + ",");
					bw.write(nameMap.get(key) + ",");
					String line = Long.toString(amountMap.get(key));
					bw.write(line);
					bw.newLine();
				}
				bw.close();
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} finally {
				if(br != null) {
					try {
						br.close();
					} catch(IOException e) {
					}
				}
			}
		}
	}
}