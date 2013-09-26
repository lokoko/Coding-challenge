package codingChallenge;
import java.io.*;
import java.net.*;

public class Client {
  public static void main(String argv[]) throws Exception {
    int dimension;
    String result;
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    Socket clientSocket = new Socket("localhost", 8000);
    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    dimension = Integer.parseInt(in.readLine());
    out.writeBytes(String.valueOf(dimension) + "\n");
    result = inFromServer.readLine();
    System.out.println("FROM SERVER: " + result);
    clientSocket.close();
  }
}
