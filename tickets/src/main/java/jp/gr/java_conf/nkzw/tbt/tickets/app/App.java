package jp.gr.java_conf.nkzw.tbt.tickets.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import jp.gr.java_conf.nkzw.tbt.tickets.batch.ReserveTicketsBatch;
import jp.gr.java_conf.nkzw.tbt.tickets.batch.ReserveTicketsBatchArgument;
import jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity.Seats;

/**
 * Ticket Reservation System GUI Application
 *
 * This class creates a simple GUI for the ticket reservation system.
 * It includes a title label and a button to start the reservation process.
 * The GUI is created using Swing components.
 * The main method initializes the GUI and sets the look and feel.
 * The class also includes a nested class for the seat panel, which displays
 * the available seats and their status.
 * The class implements ActionListener to handle button click events.ïø
 * The GUI includes a status text area to display messages and a controller
 * panel
 * with buttons to prepare, assign, and show the reservation status.
 * The class uses a SwingWorker to perform the assignment of seats in a
 * background thread, allowing the GUI to remain responsive during the process.
 * The class also includes methods to refresh the seat panel and display pending
 * applications.
 *
 * チケット予約システムのGUIアプリケーションです。
 * このクラスは、Swingコンポーネントを使って簡単なGUIを作成します。
 * タイトルラベルと予約処理を開始するボタンを含みます。
 * mainメソッドでGUIを初期化し、ルックアンドフィールを設定します。
 * 座席パネルの内部クラスがあり、利用可能な座席とその状態を表示します。
 * ActionListenerを実装し、ボタンのクリックイベントを処理します。
 * ステータステキストエリアでメッセージを表示し、コントローラパネルには
 * 準備・割当・表示ボタンがあります。
 * SwingWorkerを使って座席の割当処理をバックグラウンドで実行し、
 * GUIの応答性を保ちます。
 * 座席パネルや保留中の申請情報を更新するメソッドも含みます。
 */
public class App extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private static final Font STATUS_FONT = new Font("arial", Font.PLAIN, 24);

    private static final int MAX_TICKETS_PER_APPLICATION = 4;

    public static void main(String[] args) {

        String[] defaultArgs = {
                "-f", "show",
                "--rowSeat", "10", "10",
                "--threadSize", "4",
        };
        args = args.length == 0 ? defaultArgs : args;
        // パラメータのパース
        var argument = new ReserveTicketsBatchArgument();
        var commander = JCommander.newBuilder()
                .programName(ReserveTicketsBatch.class.getName())
                .addObject(argument).build();
        commander.parse(args);

        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOG.info("endpoint: " + argument.getEndpoint());
        App app = new App(argument);
        try {
            app.reselectSeats();
        } catch (IOException | InterruptedException e) {
            LOG.error("Error in main method", e);
            e.printStackTrace();
        }
        app.setVisible(true);
    }

    // private static ReserveTicketsBatchArgument getArgument(int row, int seat, int
    // threadSize) {
    // // 引数の設定
    // var argument = new ReserveTicketsBatchArgument();
    // argument.setFunction("show");
    // argument.setEndpoint("tcp://localhost:12345");
    // argument.setTimeout(300L);
    // argument.setThreadSize(threadSize);
    // argument.setRowSeat(Arrays.asList(row, seat));
    // return argument;
    // }

    private ReserveTicketsBatch reserveTicketsBatch;
    private JPanel controllerPanel;
    private JTextArea pendingText;
    private JButton prepareButton;
    private JButton assignButton;
    private JButton showButton;

    private JTextArea statusText;

    private SeatPanel seatPanel;

    public App(ReserveTicketsBatchArgument argument) {

        // データアクセスはバッチクラスを利用
        this.reserveTicketsBatch = new ReserveTicketsBatch(argument);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(10, 10, 1200, 800);
        setTitle("Ticket Reservation System");

        // タイトルパネル
        var titlePanel = createTitlePanel();
        getContentPane().add(titlePanel, BorderLayout.NORTH);       

        // シートパネル
        this.seatPanel = new SeatPanel();
        getContentPane().add(this.seatPanel, BorderLayout.CENTER);

        // コントロールパネル
        this.controllerPanel = createControllerPanel();
        getContentPane().add(this.controllerPanel, BorderLayout.WEST);

        // ステータスパネル
        this.statusText = createStatusText();
        getContentPane().add(this.statusText, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(200, 60));
        titlePanel.setLayout(new GridLayout(1, 2));
        titlePanel.setBackground(Color.LIGHT_GRAY);
        titlePanel.setBorder(BorderFactory.createTitledBorder("Title"));

        JLabel titleLabel = new JLabel("Ticket Reservation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        titleLabel.setForeground(Color.BLUE);
        titlePanel.add(titleLabel);

        JLabel endpointLabel = new JLabel("endpoint: " + reserveTicketsBatch.argument.getEndpoint());
        endpointLabel.setPreferredSize(new Dimension(50, 50));
        endpointLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        endpointLabel.setHorizontalAlignment(JLabel.RIGHT);
        // endpointLabel.setBorder(BorderFactory.createTitledBorder("endpoint"));
        endpointLabel.setForeground(Color.BLUE);
        titlePanel.add(endpointLabel);


        return titlePanel;
    }

    private void reselectSeats() throws IOException, InterruptedException {
        this.seatPanel.setSeats(reserveTicketsBatch.getAllSeats());
        this.seatPanel.revalidate();
        this.seatPanel.repaint();
        // pending
        var val = getPendingInfo();
        this.pendingText.setText(val);
        this.controllerPanel.repaint();
    }

    private String getPendingInfo() throws IOException, InterruptedException {
        var pending = reserveTicketsBatch.getPendingApplications();
        StringBuilder sb = new StringBuilder();
        for (var application : pending) {
            sb.append("id: ").append(application.getId()).append(", ");
            sb.append("qty: ").append(application.getApplyNum()).append("\n");
        }
        sb.append("total: ").append(pending.size()).append("\n");
        return sb.toString();
    }

    private JTextArea createStatusText() {
        statusText = new JTextArea("Wellcome Ticket Reservation System");
        statusText.setFont(STATUS_FONT);
        statusText.setSize(200, 200);
        statusText.setLineWrap(true);
        return statusText;
    }

    private JPanel createControllerPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 100));
        panel.setLayout(new GridLayout(4, 1));
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setBorder(BorderFactory.createTitledBorder("Controller"));

        pendingText = new JTextArea();
        pendingText.setLineWrap(true);
        JScrollPane scrollpane = new JScrollPane(pendingText);
        scrollpane.setBorder(new TitledBorder("pending applications"));
        panel.add(scrollpane);

        panel.add(createPreparePanel(), BorderLayout.CENTER);

        panel.add(createAssignPanel(), BorderLayout.CENTER);

        showButton = new JButton("Show");
        showButton.addActionListener(e -> {
            LOG.info(getName() + "pushed showButton");
            try {
                reselectSeats();
            } catch (IOException | InterruptedException e1) {
                e1.printStackTrace();
                errorDialog(e1.getMessage());
            }
        });
        panel.add(showButton);

        return panel;
    }

    private void errorDialog(String message) {

        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        LOG.error("Error: " + message);
    }

    private JPanel createPreparePanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 100));
        panel.setLayout(new GridLayout(1, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Prepare"));

        JPanel rowSeatPanel = new JPanel();
        rowSeatPanel.setLayout(new GridLayout(2, 1));

        JTextArea rowTextArea = new JTextArea(String.valueOf(reserveTicketsBatch.argument.getRowSeat().get(0)));
        rowTextArea.setBorder(new TitledBorder("row"));
        rowSeatPanel.add(rowTextArea);

        JTextArea seatTextArea = new JTextArea(String.valueOf(reserveTicketsBatch.argument.getRowSeat().get(1)));
        seatTextArea.setBorder(new TitledBorder("sea"));
        rowSeatPanel.add(seatTextArea);

        panel.add(rowSeatPanel);

        prepareButton = new JButton("Prepare");
        prepareButton.addActionListener(e -> {
            LOG.info(getName() + "pushed prepareButton");
            prepareButton.setText("doing...");
            prepareButton.repaint();
            prepareButton.setEnabled(false);

            reserveTicketsBatch.argument.setRowSeat(Arrays.asList(Integer.parseInt(rowTextArea.getText()),
                    Integer.parseInt(seatTextArea.getText())));
            this.seatPanel.setSeatsLayout();
            try {
                reserveTicketsBatch.prepareSeats(
                        reserveTicketsBatch.argument.getRowSeat().get(0),
                        reserveTicketsBatch.argument.getRowSeat().get(1));
                reserveTicketsBatch.prepareApplications(
                        reserveTicketsBatch.argument.getRowSeat().get(0)
                                * reserveTicketsBatch.argument.getRowSeat().get(1),
                        MAX_TICKETS_PER_APPLICATION);
                // redrae seat panel
                reselectSeats();
            } catch (IOException | InterruptedException e1) {
                e1.printStackTrace();
                errorDialog(e1.getMessage());
            }
            prepareButton.setText("Prepare");
            prepareButton.setEnabled(true);
        });
        panel.add(prepareButton);

        return panel;
    }

    private JPanel createAssignPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 100));
        panel.setLayout(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Assign"));

        JComboBox<Integer> comboBox = new JComboBox<>();
        comboBox.setBorder(new TitledBorder("thread size"));
        for (int i = 1; i <= 64; i++) {
            comboBox.addItem(i);
        }
        comboBox.setSelectedItem(reserveTicketsBatch.argument.getThreadSize());
        comboBox.addActionListener(e -> {
            reserveTicketsBatch.argument.setThreadSize((Integer) comboBox.getSelectedItem());
            LOG.info("Thread size changed to: " + reserveTicketsBatch.argument.getThreadSize());
        });
        panel.add(comboBox);

        assignButton = new JButton("Assign");
        assignButton.addActionListener(e -> {
            LOG.info(getName() + "pushed assignButton");
            statusText.setText("assigning...");
            assignButton.setEnabled(false);

            var worker = new AssignWorker();
            worker.execute();
        });
        panel.add(assignButton);

        return panel;
    }

    class AssignWorker extends SwingWorker<Long, Void> {
        AssignWorker() {
            super();
        }

        @Override
        protected Long doInBackground() throws Exception {
            assignButton.setText("doing...");
            var ret = reserveTicketsBatch.allocSeats();
            return ret;
        }

        @Override
        protected void done() {
            try {
                long eraps = get();
                LOG.info("Assignment completed");
                assignButton.setText("Assign");
                assignButton.setEnabled(true);
                // Refresh seat panel and pending info after assignment
                statusText.setText(String.format("eraps %,d ms", eraps));
                reselectSeats();
            } catch (Exception e) {
                LOG.error("Error in worker thread", e);
                statusText.setText("Error: " + e.getMessage());
                errorDialog(e.getMessage());
            }
        }
    }

    public class SeatPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        java.util.List<JLabel> seatLabels;

        public SeatPanel() {
            setBackground(Color.darkGray);
            setLayout(new GridLayout(reserveTicketsBatch.argument.getRowSeat().get(0),
                    reserveTicketsBatch.argument.getRowSeat().get(1)));
            setBorder(BorderFactory.createTitledBorder("Seat"));
            this.seatLabels = new ArrayList<JLabel>();
            var border = new LineBorder(Color.BLACK, 1);
            for (int i = 0; i < reserveTicketsBatch.argument.getRowSeat().get(0); i++) {
                for (int j = 0; j < reserveTicketsBatch.argument.getRowSeat().get(1); j++) {
                    var label = new JLabel("0");
                    seatLabels.add(label);
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setBorder(border);
                    label.setForeground(Color.WHITE);
                    add(label);
                }
            }
        }

        public void setSeatsLayout() {
            this.removeAll();
            setLayout(new GridLayout(reserveTicketsBatch.argument.getRowSeat().get(0),
                    reserveTicketsBatch.argument.getRowSeat().get(1)));
            setBorder(BorderFactory.createTitledBorder("Seat"));
            this.seatLabels = new ArrayList<JLabel>();
            var border = new LineBorder(Color.BLACK, 1);
            for (int i = 0; i < reserveTicketsBatch.argument.getRowSeat().get(0); i++) {
                for (int j = 0; j < reserveTicketsBatch.argument.getRowSeat().get(1); j++) {
                    var label = new JLabel("0");
                    seatLabels.add(label);
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setBorder(border);
                    label.setForeground(Color.WHITE);
                    add(label);
                }
            }
        }

        public void setSeats(java.util.List<Seats> seats) {
            for (int i = 0; i < seats.size(); i++) {
                if (seats.size() > seatLabels.size()) {
                    break;
                }
                var val = seats.get(i).getAssignedApplicationId();
                if (val == 0) {
                    seatLabels.get(i).setForeground(Color.RED);
                } else {
                    seatLabels.get(i).setForeground(Color.GREEN);
                }
                seatLabels.get(i).setText(String.valueOf(val));
            }
        }
    }
}
