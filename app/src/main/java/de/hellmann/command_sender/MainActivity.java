package de.hellmann.command_sender;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import java.util.List;

import de.hellmann.command_sender.ssh.SshCommander;

public class MainActivity extends AppCompatActivity {

    private static final String COMMAND = "";
    private static final String USERNAME = "";
    private static final String HOST = "";
    private static final int SSH_PORT = 0;
    private static final String PRIVATE_KEY_PATH = "";
    private static final String KEY_PASSPHRASE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonSsh = (Button) findViewById(R.id.buttonSsh);
        final TextView textView = (TextView) findViewById(R.id.textView);
        buttonSsh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SshCommander ssh = new SshCommander();
                try{
                    List<String> lines =
                            ssh.sendCommandWithKeyAuthentication(
                                    COMMAND,
                                    USERNAME,
                                    HOST,
                                    SSH_PORT,
                                    PRIVATE_KEY_PATH,
                                    KEY_PASSPHRASE);
                    String string = "";
                    for (String s : lines)
                    {
                        string += s + "\n";
                    }
                    textView.setText(string);
                }
                catch(Exception ex){
                    String string = ex.getMessage();
                    textView.setText(string);
                }

            }
        });

        /*Button buttonSms = (Button) findViewById(R.id.buttonSms);
        buttonSsh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String phoneNumber = "+4917697313425";
                String message = "Hello, this is a test";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
                intent.putExtra("sms_body", message);
                startActivity(intent);
            }
        });*/


        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
