package jp.gr.java_conf.nkzw.tbt.tickets.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
public class App extends JFrame implements ActionListener {
    public class SeatPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        java.util.List<JLabel> seatLabels;

        public SeatPanel() {
            setBackground(Color.darkGray);
            setLayout(new GridLayout(argument.getRowSheet().get(0),
                    argument.getRowSheet().get(1)));
            setBorder(BorderFactory.createTitledBorder("Seat"));
            this.seatLabels = new ArrayList<JLabel>();
            var border = new LineBorder(Color.BLACK, 1);
            for (int i = 0; i < argument.getRowSheet().get(0); i++) {
                for (int j = 0; j < argument.getRowSheet().get(1); j++) {
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
                seatLabels.get(i).setText(String.valueOf(seats.get(i).getAssignedApplicationId()));
            }
        }
    }

    class AssignWorker extends SwingWorker<Void, Void> {
        AssignWorker() {
            super();
        }

        @Override
        protected Void doInBackground() throws Exception {
            assignButton.setText("Assigning...");
            reserveTicketsBatch.allocSeats();
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                assignButton.setText("Assign");
                LOG.info("Assignment completed");
                // Refresh seat panel and pending info after assignment
                reselectSeats();
                // seatPanel.setSeats(reserveTicketsBatch.getAllSeats());
                // seatPanel.revalidate();
                // seatPanel.repaint();
                // pendingText.setText(getPendingInfo());
                // statusText.setText("ready");
                // controllerPanel.repaint();
            } catch (Exception e) {
                LOG.error("Error in worker thread", e);
                statusText.setText("Error: " + e.getMessage());
            }
        }
    }

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private static final Font STATUS_FONT = new Font("arial", Font.PLAIN, 24);

    public static void main(String[] args) throws IOException, InterruptedException {

        // String rowInput = JOptionPane.showInputDialog("列数を入力してください");
        // if (rowInput == null) {
        // System.out.println("キャンセルされました。");
        // return;
        // }
        // int rowCount = Integer.parseInt(rowInput);

        // String sheetInput = JOptionPane.showInputDialog("席数を入力してください");
        // if (sheetInput == null) {
        // System.out.println("キャンセルされました。");
        // return;
        // }
        // int sheetCount = Integer.parseInt(sheetInput);

        int rowCount = 20;
        int sheetCount = 20;
        int threadSize = 8;

        var argument = getArgument(rowCount, sheetCount, threadSize);

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

        App app = new App(argument);
        app.reselectSeats();
        app.setVisible(true);
    }

    private static ReserveTicketsBatchArgument getArgument(int row, int sheet, int threadSize) {
        // 引数の設定
        var argument = new ReserveTicketsBatchArgument();
        argument.setFunction("show");
        argument.setEndpoint("tcp://localhost:12345");
        argument.setTimeout(300L);
        argument.setThreadSize(threadSize);
        argument.setRowSheet(Arrays.asList(row, sheet));
        return argument;
    }

    private ReserveTicketsBatchArgument argument;

    private ReserveTicketsBatch reserveTicketsBatch;
    private java.util.List<Seats> seats;
    private JPanel controllerPanel;
    private JTextArea pendingText;
    private JButton prepareButton;
    private JButton assignButton;
    private JButton showButton;

    private JTextArea statusText;

    private SeatPanel seatPanel;

    public App(ReserveTicketsBatchArgument argument) {
        this.argument = argument;
        this.reserveTicketsBatch = new ReserveTicketsBatch(argument);
        this.seats = new ArrayList<Seats>();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(10, 10, 1000, 500);
        setTitle("Practice");

        this.seatPanel = new SeatPanel();
        getContentPane().add(this.seatPanel, BorderLayout.CENTER);

        this.controllerPanel = createControllerPanel();
        getContentPane().add(this.controllerPanel, BorderLayout.WEST);

        this.statusText = createStatusText();
        getContentPane().add(this.statusText, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.info(getName() + "pushed button");
        try {
            if (e.getSource() == prepareButton) {
                statusText.setText("preparering...");
                int row = argument.getRowSheet().get(0);
                int seat = argument.getRowSheet().get(1);
                reserveTicketsBatch.prepareSeats(row, seat);
                reserveTicketsBatch.prepareApplications(row * seat, 4);
            } else if (e.getSource() == assignButton) {
                statusText.setText("assigning...");
                var worker = new AssignWorker();
                worker.execute();
            } else if (e.getSource() == showButton) {
                statusText.setText("showing...");
            }
            reselectSeats();
        } catch (IOException | InterruptedException e1) {
            this.statusText.setText(e1.getMessage());
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void reselectSeats() throws IOException, InterruptedException {
        this.seatPanel.setSeats(reserveTicketsBatch.getAllSeats());
        this.seatPanel.revalidate();
        this.seatPanel.repaint();
        // pending
        var val = getPendingInfo();
        this.pendingText.setText(val);
        this.statusText.setText("ready");
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
        panel.setLayout(new GridLayout(6, 1));
        panel.setBackground(Color.YELLOW);
        panel.setBorder(BorderFactory.createTitledBorder("Controller"));

        pendingText = new JTextArea();
        pendingText.setLineWrap(true);
        JScrollPane scrollpane = new JScrollPane(pendingText);
        scrollpane.setBorder(new TitledBorder("pending num"));
        panel.add(scrollpane);

        prepareButton = new JButton("Prepare");
        prepareButton.addActionListener(this);
        panel.add(prepareButton);

        assignButton = new JButton("Assign");
        // assignButton.addActionListener(this);
        assignButton.addActionListener(e -> {
            LOG.info(getName() + "pushed assignButton");
            // worker.execute();
            var worker = new AssignWorker();
            worker.execute();
        });
        panel.add(assignButton);

        showButton = new JButton("Show");
        panel.add(showButton);
        showButton.addActionListener(this);

        return panel;
    }
}
