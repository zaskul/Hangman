import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class Hangman extends JFrame {
    private JPanel Main;
    private JPanel Buttons;
    private JPanel ButtonsPanel1;
    private JPanel ButtonsPanel2;
    private JPanel ButtonsPanel3;
    private JPanel ButtonsPanel4;
    private JPanel ImgPanel;
    private JButton DifficultySetButton;
    private JComboBox DiffSelector;
    private JLabel DiffLabel;
    private JPanel StartingScreen;
    private JPanel GameOverScreen;
    private JButton PlayAgainButton;
    private JPanel KeywordPanel;
    private JPanel GameStatusLabel;
    private JPanel PlayAgainButtonPanel;
    private JPanel Scoreboard;
    private JSplitPane Player;
    private JSplitPane Score;
    private Integer counter;
    private List<String> keyword;
    private List<String> dummyKeyword;

    private final List<String> KeywordsDifficultyEasy = new ArrayList<>(Arrays.asList(
            "data","it","system","user","key","cpu","bot","applet","bit","bridge",
            "byte","cache","CGI","client","cloud","cookie","CPU","cursor","CSS","DHCP","DNS","domain",
            "DVD","file", "folder","FTP","GIF","GPS","GUI","HTML","HTTP","Virus"));

    private final List<String> KeywordsDifficultyMedium = new ArrayList<>(Arrays.asList(
            "computer","software","internet","database","network","computing","hardware","intranet","technology","computers","digital","analysis",
            "scientific","devices","technical","science","firmware","algorithm","spyware","programmer","assembler","malware","hackathon","ASCII file",
            "attachment","bandwidth","bluetooth","bookmark","database","desktop","directory","download","emoticon","encryption","Ethernet","Web Browser"));

    private final List<String> KeywordsDifficultyHard = new ArrayList<>(Arrays.asList(
            "information system","electronics","information","engineering","informatics","computer science","cyberinformation","computer programming",
            "developing","intelligence","information and communications technology","telecommunications","spreadsheet","processor","microprocessor","systematization",
            "cybernetwork","cybertechnology","supercomputer","microcomputer","software engineer","computer system","machine code","text editor","digital data","object orient program",
            "computer circuit","application","authentication","defragmentation","flash drive","fragmentation"));

    private final List<List> KeywordsList = new ArrayList<>(
            Arrays.asList(KeywordsDifficultyEasy, KeywordsDifficultyMedium, KeywordsDifficultyHard)
    );

    private final List<String> letters = new ArrayList<String>(
            Arrays.asList("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z")
    );

    private final Map<String, Integer>  DifficultyInteger = new HashMap<String, Integer>(){
        {
            put("easy", 0);
            put("medium", 1);
            put("hard", 2);
        }
    };

    private final List<JPanel> BtnPanels = new ArrayList<JPanel>(
            Arrays.asList(ButtonsPanel1, ButtonsPanel2, ButtonsPanel3, ButtonsPanel4)
    );

    public void print(Object x){
        System.out.println(x);
    }

    public String toString(Integer val){
        return val.toString();
    }

    public void refresh(Component component){       // refresh given component
        component.revalidate();
        component.repaint();
    }

    public void setPlayAgainButton(){       // set game to the starting screen
                GameOverScreen.setVisible(false);
                StartingScreen.setVisible(true);
    }

    public void clearButtonPanels(){        // clears all button instances in ButtonsPanel 1 through 4
        for(JPanel e : BtnPanels){
            e.removeAll();
        }
    }

    public List<Integer> getAllIndexes(String val, List<String> arr){       // returns a list of indexes from given array
        List<Integer> indexList = new ArrayList<Integer>();
        for(int i = 0; i < arr.size(); i++){
            if(val.equals(arr.get(i))){
                indexList.add(i);
            }
        }
        return indexList;
    }

    public void prepareGameOverScreen(Boolean result){      // depending on the result prepares the screen that is shown after the game ends
        ImgPanel.remove(2);
        refresh(ImgPanel);
        clearButtonPanels();
        createScoreboardFile();
        KeywordPanel.remove(0);
        try{
            Scoreboard.removeAll();
            GameStatusLabel.removeAll();
            PlayAgainButtonPanel.removeAll();
        }
        catch (Exception ignored){}
        if(result){
            GameStatusLabel.add(new JLabel("VICTORY"));
            JButton btn = new JButton("Play again");
            btn.addActionListener(e -> setPlayAgainButton());
            PlayAgainButtonPanel.add(btn);
        }
        else{
            GameStatusLabel.add(new JLabel("DEFEAT"));
            JButton btn = new JButton("Play again");
            btn.addActionListener(e -> setPlayAgainButton());
            PlayAgainButtonPanel.add(btn);
        }

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(10, 2));
        for (int i = 0; i < 10 ;i++) {
            JLabel x = new JLabel("Player" + toString(i));
            JLabel s = new JLabel("Score" + toString(i));
            p.add(x, i, 0);
            p.add(s, i, 1);
        }
        Scoreboard.add(p);
        GameOverScreen.setVisible(true);
    }

    public void checkIfInKeyword(JButton btn){      // checks if the selected letter is in the keyword and acts accordingly
        String letter = btn.getActionCommand();
        if(!(keyword.contains(letter))){
            btn.setBackground(Color.red);
            btn.setEnabled(false);
            counter += 1;
            if( counter < 9 ){
                try{
                    String path = "out/production/Hangman/img/s" + counter + ".jpg";
                    ImgPanel.remove(2);
                    refresh(ImgPanel);
                    BufferedImage img = ImageIO.read(new File(path));
                    ImgPanel.add(new JLabel(new ImageIcon(img)));
                    refresh(ImgPanel);
                }
                catch (IOException e){
                }
            }
            else{
                prepareGameOverScreen(false);
            }
        }
        else{
            List<Integer> tempIndexes = getAllIndexes(letter, keyword);
            for (Integer tempIndex : tempIndexes)
                dummyKeyword.set(tempIndex, letter);
            btn.setBackground(Color.green);
            btn.setEnabled(false);
            generateKeywordLabel();
            if(dummyKeyword.equals(keyword)){
                prepareGameOverScreen(true);
            }
        }
    }

    public void generateKeywordLabel(){     // displays blank spaces as well as correctly guessed letters of the keyword at the top of the screen
        String kw = "";

        for(String k : dummyKeyword){
            kw += k + " ";
        }
        JLabel KeywordLabel = new JLabel(kw);
        try{
            KeywordPanel.remove(0);
            KeywordPanel.add(KeywordLabel);
        }
        catch (IndexOutOfBoundsException x){
            KeywordPanel.add(KeywordLabel);
        }
        refresh(KeywordPanel);
    }

    public void generateKeyword(){      // generates the keyword at the start of the game
        Random rand = new Random();
        String diffValue = DiffSelector.getSelectedItem().toString();
        List<String> selectedKeywordList = KeywordsList.get(DifficultyInteger.get(diffValue));
        String temp = (selectedKeywordList.get(rand.nextInt(selectedKeywordList.size()+1))).toString().toUpperCase(Locale.ROOT);
        print(temp);
        dummyKeyword = new ArrayList<String>();
        keyword = Arrays.asList(temp.split(""));
        for(String ignored : keyword)
            dummyKeyword.add("_");
    }

    public void generateLetterButtons(){    // generates and places all the letter buttons
        int j = 0;
        String currentPanel = "Panel1";

        Map<String, Integer> LetterPlacement = new HashMap<String, Integer>(){{
            put("Panel1", 6);
            put("Panel2", 7);
            put("Panel3", 7);
            put("Panel4", 6);
        }};

        for(int i = 0; i< 26; i++) {
            JButton btn = new JButton(letters.get(i));
            btn.setBounds(0, 0, 20, 20);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkIfInKeyword((JButton)e.getSource());
                }
            });

            BtnPanels.get(j).add(btn);
            LetterPlacement.put(currentPanel, LetterPlacement.get(currentPanel) - 1);
            if(LetterPlacement.get(currentPanel) == 0){
                j += 1;
                currentPanel = "Panel" + toString(j + 1);
            }
        }
    }

    public void generateFirstImg(){    // generates and places the starting image
        try {
            counter = 0;
            String localDir = System.getProperty("user.dir");
            File f = new File(localDir+"/src/img/s0.jpg");
            BufferedImage img = ImageIO.read(new FileInputStream(f.getAbsolutePath()));
            ImgPanel.add(new JLabel(new ImageIcon(img)));
        }
        catch (IOException err){
            System.out.println(err);
        }
    }

    public void checkIfFileExistsElseCreate(String fileName) {
        try {
            File myObj = new File(fileName);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void readScoreFile(String fileName) {
        try {
            File scoreFile = new File(fileName);
            Scanner myReader = new Scanner(scoreFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void writeScoreFile(String fileName){
        try {
            checkIfFileExistsElseCreate(fileName);
            FileWriter scoreWriter = new FileWriter(fileName);
            scoreWriter.write("Files in Java might be tricky, but it is fun enough!");
            scoreWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void createScoreboardFile(){
        readScoreFile("scores.txt");
        writeScoreFile("scores.txt");
    }

    public Hangman(){
        GameOverScreen.setVisible(false);
        DifficultySetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateKeyword();
                generateKeywordLabel();
                generateFirstImg();
                generateLetterButtons();
                StartingScreen.setVisible(false);
            }
        });
    }

    public static void main(String args[]){
        Hangman h = new Hangman();
        h.setContentPane(new Hangman().Main);
        h.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        h.setVisible(true);
        h.setSize(700,900);
    }
}
