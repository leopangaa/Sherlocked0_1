package view.screens;

import core.GameState;
import main.MainGame;
import utils.UiScale;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InventoryPanel extends JPanel {

    private final DefaultListModel<String> listModel;
    private final JList<String> clueList;
    private final JTextArea clueDetailsArea;
    private String returnTo;

    // Temporary clue descriptions while clues are still stored as String
    private final Map<String, String> clueDescriptions = new HashMap<>();

    public InventoryPanel() {
        setLayout(null);
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 180));

        seedClueDescriptions();

        int panelW = UiScale.w(900);
        int panelH = UiScale.h(520);
        int panelX = (UiScale.GAME_WIDTH - panelW) / 2;
        int panelY = (UiScale.GAME_HEIGHT - panelH) / 2;

        JPanel bookPanel = new JPanel(null);
        bookPanel.setBounds(panelX, panelY, panelW, panelH);
        bookPanel.setBackground(new Color(20, 18, 16));
        bookPanel.setBorder(new LineBorder(new Color(120, 105, 80), 3));
        add(bookPanel);

        JLabel title = new JLabel("Inventory / Clues");
        title.setForeground(new Color(245, 235, 220));
        title.setFont(new Font("Serif", Font.BOLD, UiScale.font(24)));
        title.setBounds(UiScale.x(25), UiScale.y(20), UiScale.w(300), UiScale.h(35));
        bookPanel.add(title);

        JLabel subtitle = new JLabel("Collected evidence and observations");
        subtitle.setForeground(new Color(180, 170, 155));
        subtitle.setFont(new Font("Serif", Font.ITALIC, UiScale.font(12)));
        subtitle.setBounds(UiScale.x(28), UiScale.y(50), UiScale.w(300), UiScale.h(20));
        bookPanel.add(subtitle);

        JButton back = new JButton("Close");
        back.setFont(new Font("Serif", Font.BOLD, UiScale.font(15)));
        back.setFocusPainted(false);
        back.setBackground(new Color(200, 190, 170));
        back.setForeground(new Color(30, 25, 20));
        back.setBounds(panelW - UiScale.w(140), UiScale.y(18), UiScale.w(105), UiScale.h(35));
        back.addActionListener(e -> MainGame.getInstance().closeInventory(returnTo));
        bookPanel.add(back);

        JPanel leftPanel = new JPanel(null);
        leftPanel.setBounds(UiScale.x(25), UiScale.y(85), UiScale.w(260), UiScale.h(400));
        leftPanel.setBackground(new Color(32, 28, 24));
        leftPanel.setBorder(new LineBorder(new Color(90, 80, 65), 2));
        bookPanel.add(leftPanel);

        JLabel cluesLabel = new JLabel("Clues");
        cluesLabel.setForeground(new Color(235, 225, 205));
        cluesLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(18)));
        cluesLabel.setBounds(UiScale.x(15), UiScale.y(10), UiScale.w(100), UiScale.h(25));
        leftPanel.add(cluesLabel);

        listModel = new DefaultListModel<>();
        clueList = new JList<>(listModel);
        clueList.setFont(new Font("Serif", Font.PLAIN, UiScale.font(16)));
        clueList.setBackground(new Color(40, 34, 30));
        clueList.setForeground(new Color(245, 235, 220));
        clueList.setSelectionBackground(new Color(90, 76, 58));
        clueList.setSelectionForeground(Color.WHITE);
        clueList.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        clueList.setFixedCellHeight(UiScale.h(32));

        JScrollPane listScroll = new JScrollPane(clueList);
        listScroll.setBounds(UiScale.x(10), UiScale.y(45), UiScale.w(240), UiScale.h(340));
        listScroll.setBorder(new LineBorder(new Color(90, 80, 65), 1));
        leftPanel.add(listScroll);

        JPanel rightPanel = new JPanel(null);
        rightPanel.setBounds(UiScale.x(305), UiScale.y(85), UiScale.w(570), UiScale.h(400));
        rightPanel.setBackground(new Color(28, 24, 22));
        rightPanel.setBorder(new LineBorder(new Color(90, 80, 65), 2));
        bookPanel.add(rightPanel);

        JLabel detailsLabel = new JLabel("Details");
        detailsLabel.setForeground(new Color(235, 225, 205));
        detailsLabel.setFont(new Font("Serif", Font.BOLD, UiScale.font(18)));
        detailsLabel.setBounds(UiScale.x(15), UiScale.y(10), UiScale.w(100), UiScale.h(25));
        rightPanel.add(detailsLabel);

        clueDetailsArea = new JTextArea();
        clueDetailsArea.setEditable(false);
        clueDetailsArea.setLineWrap(true);
        clueDetailsArea.setWrapStyleWord(true);
        clueDetailsArea.setFont(new Font("Serif", Font.PLAIN, UiScale.font(16)));
        clueDetailsArea.setForeground(new Color(240, 232, 220));
        clueDetailsArea.setBackground(new Color(36, 31, 28));
        clueDetailsArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        clueDetailsArea.setText("Select a clue to inspect it.");

        JScrollPane detailsScroll = new JScrollPane(clueDetailsArea);
        detailsScroll.setBounds(UiScale.x(10), UiScale.y(45), UiScale.w(550), UiScale.h(340));
        detailsScroll.setBorder(new LineBorder(new Color(90, 80, 65), 1));
        rightPanel.add(detailsScroll);

        clueList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = clueList.getSelectedValue();
                if (selected != null) {
                    clueDetailsArea.setText(getClueDescription(selected));
                }
            }
        });

        GameState.getInstance().addListener(this::refresh);
    }

    public void open(String returnTo) {
        this.returnTo = returnTo;
        refresh();

        if (!listModel.isEmpty() && !listModel.get(0).equals("No clues collected yet.")) {
            clueList.setSelectedIndex(0);
        } else {
            clueDetailsArea.setText("You have not collected any clues yet.");
        }
    }

    public void refresh() {
        listModel.clear();
        ArrayList<String> clues = GameState.getInstance().clues;

        if (clues.isEmpty()) {
            listModel.addElement("No clues collected yet.");
            clueDetailsArea.setText("You have not collected any clues yet.");
        } else {
            for (String clue : clues) {
                listModel.addElement("• " + clue);
            }
        }
    }

    private String getClueDescription(String selectedValue) {
        String clean = selectedValue.replaceFirst("^•\\s*", "");
        return clueDescriptions.getOrDefault(
                clean,
                clean + "\n\nA clue collected during the investigation. Its meaning is still unclear."
        );
    }

    private void seedClueDescriptions() {
        clueDescriptions.put("Guest Register entry",
                "Guest Register Entry\n\n" +
                "The front desk register contains today's hotel entries.\n\n" +
                "- Dr. Kells' name is crossed out in red ink.\n" +
                "- Someone wrote the word 'LIAR' beside it.\n" +
                "- Another entry appears under the name 'E. Vane' in Room 305.\n\n" +
                "This may suggest someone tampered with the hotel's official records.");

        clueDescriptions.put("Mirror reflection hint",
                "Mirror Reflection Hint\n\n" +
                "A strange message appeared on the mirror: 'YOU ARE NOT ALONE.'\n\n" +
                "The reflection also seemed delayed, as if something in the room was out of sync.\n\n" +
                "This could be psychological... or something worse.");

        clueDescriptions.put("Frozen Clock hint",
                "Frozen Clock Hint\n\n" +
                "The clock is stuck at 11:45, but its second hand appears to move backwards.\n\n" +
                "Time distortion or symbolic clue?\n\n" +
                "It may connect to the moment of the incident.");

        clueDescriptions.put("Blackwood Suitcase hint",
                "Blackwood Suitcase Hint\n\n" +
                "An old suitcase tagged 'R. Blackwood' was found in the lobby lounge.\n\n" +
                "A strange symbol was carved into the leather.\n" +
                "The same symbol appeared elsewhere in the hotel.\n\n" +
                "The owner may be tied to the hotel mystery.");

        clueDescriptions.put("Mysterious note",
                "Mysterious Note\n\n" +
                "Liam handed over a note dropped near Room 217.\n\n" +
                "He said the person who dropped it looked familiar.\n\n" +
                "This clue may connect directly to the detective's investigation.");

        // Floor 1 Clues
        clueDescriptions.put("Harper Testimony",
                "Harper Testimony\n\n" +
                "Ms. Harper claims she saw someone leave Room 217.\n\n" +
                "She described them as wearing 'something dark,' but she seemed unsure of herself.\n\n" +
                "Is she hiding something, or truly terrified?");

        clueDescriptions.put("Doyle Statement",
                "Doyle Statement\n\n" +
                "Mr. Doyle insists no one entered or left the room all night.\n\n" +
                "He was positioned in the hallway and seems quite certain.\n\n" +
                "This directly contradicts Ms. Harper's testimony.");

        clueDescriptions.put("Strange Stain",
                "Strange Stain\n\n" +
                "A faint, discolored mark on the carpet outside Room 217.\n\n" +
                "It looks like something heavy was dragged towards the utility room.\n\n" +
                "The cleaning staff might have missed this.");

        clueDescriptions.put("Signs of Struggle",
                "Signs of Struggle\n\n" +
                "The bedding in Room 217 is heavily disturbed.\n\n" +
                "Items are knocked over, but there's no trace of blood or a struggle beyond the furniture.\n\n" +
                "It feels staged... or extremely precise.");

        clueDescriptions.put("Distorted Reflection",
                "Distorted Reflection\n\n" +
                "The mirror in Room 217 doesn't reflect things correctly.\n\n" +
                "My own image felt delayed, as if the person in the glass was a second behind my movements.\n\n" +
                "My head is starting to ache.");

        clueDescriptions.put("Therapy Notes Fragment",
                "Therapy Notes Fragment\n\n" +
                "A torn page from a patient's log found on the desk.\n\n" +
                "It mentions severe memory reconstruction issues and signs of denial.\n\n" +
                "The patient's name is missing from the fragment.");

        clueDescriptions.put("Sealed Window",
                "Sealed Window\n\n" +
                "The window in Room 217 is locked and sealed from the inside.\n\n" +
                "There is no way anyone could have entered or exited through here without breaking the glass.\n\n" +
                "This confirms the 'Locked Room' scenario.");

        clueDescriptions.put("Missing Cleaning Tool",
                "Missing Cleaning Tool\n\n" +
                "A spot in the utility cart where a heavy-duty solvent should be.\n\n" +
                "The bottle is gone, and there's a faint smell of bleach in the air.\n\n" +
                "Someone was cleaning more than just the floors.");

        clueDescriptions.put("Destroyed Note",
                "Destroyed Note\n\n" +
                "A scrap of paper found in the trash.\n\n" +
                "Only the words '...don't look back...' are visible.\n\n" +
                "It looks like it was written in a hurry.");

        clueDescriptions.put("CCTV Footage Anomaly",
                "CCTV Footage Anomaly\n\n" +
                "The footage from the hallway has a five-minute gap.\n\n" +
                "The timestamp jumps, and for a split second, a flickering figure is visible.\n\n" +
                "Someone knows how to bypass the security system.");

        // Floor 2 Clues
        clueDescriptions.put("Rina Testimony",
                "Rina Testimony\n\n" +
                "The housekeeper was incredibly nervous.\n\n" +
                "She claimed to know nothing, but her reaction to the Room 217 investigation was extreme.\n\n" +
                "She's afraid of something—or someone.");

        clueDescriptions.put("Jared Statement",
                "Jared Statement\n\n" +
                "The security guard's responses were repetitive and rehearsed.\n\n" +
                "He kept saying 'Area is secure' even when asked direct questions about the incident.\n\n" +
                "It felt like I was talking to a recording.");

        clueDescriptions.put("Abandoned Cleaning Cart",
                "Abandoned Cleaning Cart\n\n" +
                "Found in the staff hallway, left in the middle of the corridor.\n\n" +
                "It suggests the staff left in a significant hurry.\n\n" +
                "Or perhaps they were told to leave.");

        clueDescriptions.put("Staff Warning Notice",
                "Staff Warning Notice\n\n" +
                "A memo to all employees: 'REPORT ANY UNUSUAL BEHAVIOR'.\n\n" +
                "The date on the notice is from three days ago.\n\n" +
                "The management knew something was coming.");

        clueDescriptions.put("Used Bedding",
                "Used Bedding\n\n" +
                "The bed in the staff quarters was recently occupied.\n\n" +
                "The pillows are still indented, and the sheets are warm.\n\n" +
                "Someone was resting here just minutes before I arrived.");

        clueDescriptions.put("Hidden Journal",
                "Hidden Journal\n\n" +
                "A diary found hidden in a staff locker.\n\n" +
                "It contains detailed observations of a 'patient' whose behavior is becoming unpredictable.\n\n" +
                "The descriptions are disturbingly familiar.");

        clueDescriptions.put("Distorted Self Image",
                "Distorted Self Image\n\n" +
                "Another mirror, another wrong reflection.\n\n" +
                "This time, my face seemed to shift and blur into someone else's.\n\n" +
                "I can't trust my own eyes anymore.");

        clueDescriptions.put("Personal Notes",
                "Personal Notes\n\n" +
                "Notes about memory lapses and lost time.\n\n" +
                "The handwriting matches my own.\n\n" +
                "I don't remember writing these.");

        clueDescriptions.put("Access Logs",
                "Access Logs\n\n" +
                "Digital records showing who accessed Room 217.\n\n" +
                "The master key used belongs to the lead investigator.\n\n" +
                "That's me. I was in that room.");

        clueDescriptions.put("Patient Record",
                "Patient Record\n\n" +
                "A confidential file found in the records room.\n\n" +
                "It bears my name, my photo, and a diagnosis of severe trauma-induced dissociation.\n\n" +
                "I'm not the detective. I'm the subject.");

        clueDescriptions.put("Archived Reports",
                "Archived Reports\n\n" +
                "Old files describing similar 'incidents' at this hotel.\n\n" +
                "They all follow the same pattern: a death, a 'detective', and a missing memory.\n\n" +
                "This has happened before. Many times.");
    }
}