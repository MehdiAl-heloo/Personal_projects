package ToDoList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ToDoListGUI extends JFrame {
    private JTextField taskInput;
    private JButton addButton;
    private JPanel taskContainer;
    private final String SAVE_FILE = "tasks.txt";

    public ToDoListGUI() {
	setTitle("To-Do List");
	setSize(400, 550);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setLocationRelativeTo(null);
	setLayout(new BorderLayout());

	// Theme colors
	Color bgColor = new Color(30, 30, 30);
	Color taskBoxColor = new Color(50, 50, 50);
	Color fontColor = new Color(220, 220, 220);
	Color borderColor = new Color(80, 80, 80);
	Color inputColor = new Color(40, 40, 40);

	// Input panel
	JPanel inputPanel = new JPanel(new BorderLayout());
	inputPanel.setBackground(bgColor);

	taskInput = new JTextField();
	taskInput.setBackground(inputColor);
	taskInput.setForeground(fontColor);
	taskInput.setCaretColor(fontColor);
	taskInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	addButton = new JButton("Add");
	styleButton(addButton, fontColor, inputColor);
	addButton.setPreferredSize(new Dimension(80, 40));

	inputPanel.add(taskInput, BorderLayout.CENTER);
	inputPanel.add(addButton, BorderLayout.EAST);
	add(inputPanel, BorderLayout.NORTH);

	// Task container and scroll pane
	taskContainer = new JPanel();
	taskContainer.setLayout(new BoxLayout(taskContainer, BoxLayout.Y_AXIS));
	taskContainer.setBackground(bgColor);

	JPanel wrapper = new JPanel(new BorderLayout());
	wrapper.setBackground(bgColor);
	wrapper.add(taskContainer, BorderLayout.NORTH);

	JScrollPane scrollPane = new JScrollPane(wrapper);
	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	scrollPane.getVerticalScrollBar().setUnitIncrement(16);
	scrollPane.getViewport().setBackground(bgColor);
	scrollPane.setBorder(null);
	add(scrollPane, BorderLayout.CENTER);

	// Add task listeners
	addButton.addActionListener(e -> addTaskFromInput(taskBoxColor, fontColor, borderColor));
	taskInput.addActionListener(e -> addTaskFromInput(taskBoxColor, fontColor, borderColor));

	// Load tasks from file
	loadTasksFromFile(taskBoxColor, fontColor, borderColor);

	// Save tasks when window closes
	addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e) {
		saveTasksToFile();
	    }
	});
    }

    private void styleButton(JButton button, Color fontColor, Color bgColor) {
	button.setFocusPainted(false);
	button.setBackground(bgColor);
	button.setForeground(fontColor);
	button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
	button.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    private void addTaskFromInput(Color taskBoxColor, Color fontColor, Color borderColor) {
	String taskText = taskInput.getText().trim();
	if (!taskText.isEmpty()) {
	    addTask(taskText, false, taskBoxColor, fontColor, borderColor);
	    taskInput.setText("");
	}
    }

    private void addTask(String taskText, boolean completed, Color taskBoxColor, Color fontColor, Color borderColor) {
	JPanel taskPanel = new JPanel(new BorderLayout());
	taskPanel.setBackground(taskBoxColor);
	taskPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderColor),
		BorderFactory.createEmptyBorder(10, 10, 10, 10)));

	JPanel labelPanel = new JPanel(new BorderLayout());
	labelPanel.setOpaque(false);

	JCheckBox checkBox = new JCheckBox();
	checkBox.setBackground(taskBoxColor);
	checkBox.setForeground(fontColor);
	checkBox.setSelected(completed);

	JLabel taskLabel = new JLabel();
	taskLabel.setForeground(fontColor);
	taskLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
	taskLabel.setVerticalAlignment(SwingConstants.TOP);
	taskLabel.setText(
		completed ? "<html><strike>" + taskText + "</strike></html>" : "<html>" + taskText + "</html>");

	checkBox.addActionListener(e -> {
	    if (checkBox.isSelected()) {
		taskLabel.setText("<html><strike>" + taskText + "</strike></html>");
	    } else {
		taskLabel.setText("<html>" + taskText + "</html>");
	    }
	});

	labelPanel.add(checkBox, BorderLayout.WEST);
	labelPanel.add(taskLabel, BorderLayout.CENTER);

	JButton deleteButton = new JButton("X");
	styleButton(deleteButton, fontColor, new Color(60, 60, 60));
	deleteButton.setPreferredSize(new Dimension(50, 40));
	deleteButton.setMargin(new Insets(5, 15, 5, 15));

	deleteButton.addActionListener(e -> {
	    taskContainer.remove(taskPanel);
	    taskContainer.revalidate();
	    taskContainer.repaint();
	});

	taskPanel.add(labelPanel, BorderLayout.CENTER);
	taskPanel.add(deleteButton, BorderLayout.EAST);

	enableDragAndDrop(taskPanel);
	taskContainer.add(taskPanel);
	taskContainer.revalidate();
	taskContainer.repaint();
    }

    private void enableDragAndDrop(JPanel panel) {
	final Point[] startPt = new Point[1];

	panel.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mousePressed(MouseEvent e) {
		startPt[0] = SwingUtilities.convertPoint(panel, e.getPoint(), taskContainer);
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
		Point endPt = SwingUtilities.convertPoint(panel, e.getPoint(), taskContainer);
		Component[] components = taskContainer.getComponents();
		int newIndex = -1;
		for (int i = 0; i < components.length; i++) {
		    Rectangle bounds = components[i].getBounds();
		    if (endPt.y < bounds.y + bounds.height / 2) {
			newIndex = i;
			break;
		    }
		}

		taskContainer.remove(panel);
		if (newIndex == -1 || newIndex >= taskContainer.getComponentCount()) {
		    taskContainer.add(panel);
		} else {
		    taskContainer.add(panel, newIndex);
		}
		taskContainer.revalidate();
		taskContainer.repaint();
	    }
	});
    }

    private void saveTasksToFile() {
	try (PrintWriter writer = new PrintWriter(SAVE_FILE)) {
	    for (Component comp : taskContainer.getComponents()) {
		if (comp instanceof JPanel panel) {
		    Component labelComp = panel.getComponent(0);
		    if (labelComp instanceof JPanel labelPanel) {
			Component[] children = labelPanel.getComponents();
			JCheckBox checkBox = (JCheckBox) children[0];
			JLabel label = (JLabel) children[1];
			boolean done = checkBox.isSelected();
			String text = label.getText().replaceAll("(?i)<[^>]*>", "").replace("✔ ", "").trim();
			writer.println(done + "|" + text);
		    }
		}
	    }
	} catch (Exception e) {
	    System.err.println("Failed to save tasks: " + e.getMessage());
	}
    }

    private void loadTasksFromFile(Color taskBoxColor, Color fontColor, Color borderColor) {
	File file = new File(SAVE_FILE);
	if (!file.exists()) {
	    return;
	}

	try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	    String line;
	    while ((line = reader.readLine()) != null) {
		String[] parts = line.split("\\|", 2);
		if (parts.length == 2) {
		    boolean done = Boolean.parseBoolean(parts[0]);
		    String text = parts[1];
		    addTask(text, done, taskBoxColor, fontColor, borderColor);
		}
	    }
	} catch (Exception e) {
	    System.err.println("Failed to load tasks: " + e.getMessage());
	}
    }

    public static void main(String[] args) {
	SwingUtilities.invokeLater(() -> new ToDoListGUI().setVisible(true));
    }
}