package com.suai;

import java.io.File;
import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class UDPServer {

  private DatagramSocket serverSocket;
  private String serverName = "you";
  private String clientName = "anon";
  private InetAddress ipAddressClient;
  private int portClient;
  private int portServer;
  private ArrayList<String> chatHistory;

  private class ServerWorker extends Thread {

    public void run() {
      try {
        while (true) {
          getClientMessage();
        }
      } catch (Exception e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public UDPServer() throws Exception {
    System.out.println("Type port: "); // 9876
    Scanner scanner = new Scanner(System.in);
    if (scanner.hasNextInt()) {
      portServer = scanner.nextInt();
    }
    serverSocket = new DatagramSocket(portServer);
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
  private void sendServerMessage() throws Exception {
    Scanner scanner = new Scanner(System.in);
    String serverMessage = scanner.nextLine();
    if (serverMessage.contains("@name")) {
      serverName = serverMessage.substring(6);
      serverMessage = "Companion changed name to " + serverName;
    }
    boolean is_left = false;
    if (serverMessage.contains("@quit")) {
      serverMessage = "Companion left the chat";
      is_left = true;
    }
    if (serverMessage.contains("@dump")) {
      StringBuilder filename = new StringBuilder("C:/Users/iyush/IdeaProjects/laba 11/src/com/suai/");
      filename.append(serverMessage.substring(6));
      writeToFile(filename.toString());
      return;
    }
    byte[] sendData = serverMessage.getBytes();
    if (ipAddressClient == null || portClient == 0) { // если сервер начнет отправку раньше клиента
      return;
    }
    DatagramPacket sendServerMessagePacket = new DatagramPacket(sendData, sendData.length,
        ipAddressClient,
        portClient);
    serverSocket.send(sendServerMessagePacket);
    writeToChatHistory(serverName, serverMessage);
    if (is_left) {
      System.exit(0);
    }
  }

  private void getClientMessage() throws Exception {
    byte[] clientMessageByte = new byte[100];
    DatagramPacket receiveClientMessagePacket = new DatagramPacket(clientMessageByte,
        clientMessageByte.length);
    serverSocket.receive(receiveClientMessagePacket);
    String clientMessage = new String(receiveClientMessagePacket.getData());
    if (portClient == 0 || ipAddressClient == null) {
      portClient = receiveClientMessagePacket.getPort();
      ipAddressClient = receiveClientMessagePacket.getAddress();
    }
    if (clientMessage.contains("Companion changed name to ")) {
      clientName = clientMessage.substring(26,clientMessage.indexOf(0));
    }
    System.out.println(clientName + ": " + clientMessage);
    writeToChatHistory(clientName, clientMessage.substring(0,clientMessage.indexOf(0)));
  }

  public void mainFunction() throws Exception {
    System.out.println("Hey! It's a chat!");
    System.out.println("You can type your name in format @name Name or type your message.");
    System.out.println("Type @quit to exit.");
    ServerWorker serverWorker = new ServerWorker();
    serverWorker.start();
    while (true) {
      sendServerMessage();
    }
  }


  public static void main(String[] args) {
    try {
      UDPServer server = new UDPServer();
      server.mainFunction();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

}
