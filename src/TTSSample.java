//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Speech-TTS
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

//package com.microsoft.cognitiveservices.ttssample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class TTSSample {
	
	public static void main(String[] args) {

		
		String apiKey = "8258624c13a145288390fc99cc417dde";
		System.out.println("Enter the CSV file name and directory path: ");
		Scanner sc = new Scanner(System.in);
		String csvfile = sc.nextLine();

		int senCount = 0;
		BufferedReader br = null;
		String[] medicaltext = { "" };
		String line = "";
		String csvSplitBy = "\n";
		String outputFormat = AudioOutputFormat.Riff16Khz16BitMonoPcm;
		String deviceLanguage = "en-US";
		String genderName = Gender.Female;
		String voiceName = "Microsoft Server Speech Text to Speech Voice (en-US, ZiraRUS)";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		Timer t = new Timer();
		try {

//			t.scheduleAtFixedRate(new TimerTask() {
//				public void run() {
//					Authentication auth = new Authentication("b935de0d5b9c472583a3e1ee64757d8e");
//					accessToken = auth.GetAccessToken();
//				}
//			}, 0, // run first occurrence immediately
//					540000); // run every 9 minutes
			
	    	Authentication auth = new Authentication(apiKey);
	    	System.out.println("The Authentication is "+auth);
	    	String accessToken = auth.GetAccessToken();
	    	
			br = new BufferedReader(new FileReader(csvfile));
//			byte[] audioBuffer= {};
			while ((line = br.readLine()) != null) {
				senCount++;
				System.out.println("Sentence Number: " + senCount);
				LocalDateTime now = LocalDateTime.now();
				String timestamp = dtf.format(now);
				// use next line separator
				medicaltext = line.split(csvSplitBy);
				System.out.println(medicaltext);
				String textToSynthesize = medicaltext[0];
				System.out.println(textToSynthesize);
//				System.out.println("The Authentication is "+auth);
				System.out.println(accessToken);
				
				byte[] audioBuffer = TTSService.Synthesize(textToSynthesize, outputFormat, deviceLanguage, genderName, voiceName, accessToken);
//				if (accessToken != null) {
//					audioBuffer = TTSService.Synthesize(textToSynthesize, outputFormat, deviceLanguage, genderName, voiceName, accessToken);
//				}
//				else {
//					accessToken = auth.RenewAccessToken();
//					audioBuffer = TTSService.Synthesize(textToSynthesize, outputFormat, deviceLanguage, genderName, voiceName, accessToken);
//				}
					
				String audioFileName = timestamp + "_PubMed_Pneumonia_PMID_29735821_Sentence_" + senCount;
				System.out.println(audioFileName);
				// write the pcm data to the file
				String outputWave = ".\\" + audioFileName + ".mp3";
				File outputAudio = new File(outputWave);
				FileOutputStream fstream = new FileOutputStream(outputAudio);
				fstream.write(audioBuffer);
				fstream.flush();
				fstream.close();

				// specify the audio format
				AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 1 * 2, 16000,
						false);

				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(outputWave));

				DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat,
						AudioSystem.NOT_SPECIFIED);
				SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
				sourceDataLine.open(audioFormat);
				sourceDataLine.start();
				System.out.println("start to play the wave:");
				/*
				 * read the audio data and send to mixer
				 */
				int count;
				byte tempBuffer[] = new byte[4096];
				while ((count = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) > 0) {
					sourceDataLine.write(tempBuffer, 0, count);
				}

				sourceDataLine.drain();
				sourceDataLine.close();
				audioInputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}