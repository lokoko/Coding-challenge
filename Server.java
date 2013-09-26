package codingChallenge;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

public class Server implements ThreadCompleteListener {
  private static int _min = 0;
  private static int _max = 5000;

  static HashMap<Integer, String> _existingDimensions = new HashMap<Integer, String>();

  public void main(String argv[]) throws Exception {
    ServerSocket socket = new ServerSocket(8000);
    while (true) {
      Socket connectionSocket = socket.accept();

      NotifyThread thread = new ClientSocket(connectionSocket);
      thread.addListener(this);
      thread.start();
    }
  }

  public static class ClientSocket extends NotifyThread {
    private final Socket _clientSocket;

    public ClientSocket(Socket clientSocket) {
      _clientSocket = clientSocket;
    }

    public void doRun() {
      try {
        String result = "";
        boolean showX = false;
        int dimension;
        
        BufferedReader in = new BufferedReader(new InputStreamReader(_clientSocket.getInputStream()));
        DataOutputStream out = new DataOutputStream(_clientSocket.getOutputStream());
        dimension = Integer.parseInt(in.readLine());
        
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        
        boolean exist = false;
        for (Thread thread : threadSet) {
          if (thread.getName() == String.valueOf(dimension)) {
            exist = true;
            break;
          }
        }

        if (!exist) {
          this.setName(String.valueOf(dimension));
        }
        
        if (!_existingDimensions.containsKey(dimension)) {
          _existingDimensions.put(dimension, result);
          Thread.sleep(_min + (int) (Math.random() * ((_max - _min) + 1)));
          if (dimension > 0) {
            for (int i = 0; i < dimension; ++i) {
              showX = !showX;
              for (int j = 0; j < dimension; ++j) {
                if (showX) {
                  if (j % 2 == 0) {
                    result = result + "X";
                  } else {
                    result = result + "O";
                  }
                } else {
                  if (j % 2 != 0) {
                    result = result + "X";
                  } else {
                    result = result + "O";
                  }
                }
              }
            }
            out.writeBytes(result);
            _clientSocket.close();
          }
        } else {
          if (_existingDimensions.get(dimension).length() == 0) {
            synchronized (this.getName()) {
              this.getName().wait();
            }
          }
          out.writeBytes("XXX:" + _existingDimensions.get(dimension));
          _clientSocket.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void notifyOfThreadComplete(Thread thread) {
    synchronized (thread.getName()) {
      thread.getName().notifyAll();
    }
  }
}
