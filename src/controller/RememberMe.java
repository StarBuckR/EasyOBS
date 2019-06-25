
package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RememberMe {
    public RememberMe(){
        
    }
    public void writeData(String username, String password){
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("Username", username);
        hashMap.put("Password", password);
        
        try {
            File file = new File("./userdata.ser");
            file.delete();
        } catch (Exception e) {
        }
        
        try {
            FileOutputStream fos = new FileOutputStream("./userdata.ser");
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(hashMap);
            Path path = Paths.get("./", "userdata.ser");
            Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
            out.close();
            fos.close();
        } catch (Exception e) {
        }
    }
    public void readData(JTextField jTextField, JPasswordField jPasswordField) throws FileNotFoundException {
        HashMap hashMap = null;
        try {
            FileInputStream fis = new FileInputStream("./userdata.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            hashMap = (HashMap) ois.readObject();
            jTextField.setText(hashMap.get("Username").toString());
            jPasswordField.setText(hashMap.get("Password").toString());
            ois.close();
            fis.close();
        } catch (Exception e) {
        }
    }
}
