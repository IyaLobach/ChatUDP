package com.suai;


import java.io.File;
import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class UDPClient {

  private String serverName = "anon";
  private String clientName = "you";
  private DatagramSocket clientSocket;
  private int portServer;
  private InetAddress ipAddress;
  private ArrayList<String> chatHistory;

  private class ClientWorker extends Thread {

    public void run() {
      try {
        while (true) {
          getServerMessage();
        }
      } catch (Exception e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public UDPClient() throws Exception {
    clientSocket = new DatagramSocket();
    Scanner scanner;
    System.out.println("Type ipAddress: ");
    scanner = new Scanner(System.in);
    if (scanner.hasNext()) {
      ipAddress = InetAddress.getByName(scanner.next()); // "localhost"
    }
    System.out.println("Type port: ");
    scanner = new Scanner(System.in); // 9876
    if (scanner.hasNextInt()) {
      portServer = scanner.nextInt();
    }
    chatHistory = new ArrayList<>();
  }

  private void writeToChatHistory(String name, String message) {
    synchronized (chatHistory) {
      chatHistory.add(name + ": " + message);
    }
  }
  private void writeToFile(String filename) throws  Exception {
    FileWriter writer = new FileWriter(filename);
    for (int i = 0; i < chatHistory.size(); i++) {
      writer.write(chatHistory.get(i));
      writer.write("\n");
    }
    writer.close();
  }
  private void sendClientMessage() throws Exception {
    Scanner scanner = new Scanner(System.in);
    String clientMessage = scanner.nextLine();
    if (clientMessage.contains("@name")) {
      clientName = clientMessage.substring(6);
      clientMessage = "Companion changed name to " + clientName;
    }
    boolean is_left = false;
    if (clientMessage.contains("@quit")) {
      clientMessage = "Companion left the chat";
      is_left = true;
    }
    if (clientMessage.contains("@dump")) {
      StringBuilder filename = new StringBuilder(
          "C:/Users/iyush/IdeaProjects/laba 11/src/com/suai/");
      filename.append(clientMessage.substring(6));
      writeToFile(filename.toString());
      return;
    }
    byte[] sendData = clientMessage.getBytes();
    DatagramPacket sendClientMessagePacket = new DatagramPacket(sendData, sendData.length,
        ipAddress,
        portServer);
    clientSocket.send(sendClientMessagePacket);
    writeToChatHistory(clientName, clientMessage);
    if (is_left) {
      System.exit(0);
    }
  }

  private void getServerMessage() throws Exception {
    byte[] serverMessageByte = new byte[100];
    DatagramPacket receiveServerMessagePacket = new DatagramPacket(serverMessageByte,
        serverMessageByte.length);
    clientSocket.receive(receiveServerMessagePacket);
    String serverMessage = new String(receiveServerMessagePacket.getData());
    if (serverMessage.contains("Companion changed name to ")) {
      serverName = serverMessage.substring(26, serverMessage.indexOf(0));
    }
    System.out.println(serverName + ": " + serverMessage);
    writeToChatHistory(serverName, serverMessage.substring(0,serverMessage.indexOf(0)));
  }

  public void mainFunction() throws Exception {
    System.out.println("Hey! It's a chat!");
    System.out.println("You can type your name in format @name Name or type your message.");
    System.out.println("Type @quit to exit.");
    ClientWorker clientWorker = new ClientWorker();
    clientWorker.start();
    while (true) {
      sendClientMessage();
    }
  }

  public static void main(String[] args) {
    try {
      UDPClient client = new UDPClient();
      client.mainFunction();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

}
