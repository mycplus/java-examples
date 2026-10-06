import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * A Swing animation of the recursive solution: a JPanel driven by a
 * javax.swing.Timer, so every repaint happens on the event dispatch thread.
 *
 * <pre>
 * java HanoiAnimation [N]                     open a window (N = 1..10, default 5)
 * java HanoiAnimation N --snapshot FILE [K]   write a PNG after K moves, no window
 * </pre>
 */
public final class HanoiAnimation extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 660;
    private static final int HEIGHT = 300;
    private static final Color BACKGROUND = new Color(0xF4F5F7);
    private static final Color STRUCTURE = new Color(0x9CA3AF);
    private static final Color DISK = new Color(0x1A6FD4);
    private static final Color MOVED = new Color(0x467F40);
    private static final Color TEXT = new Color(0x1F2937);

    private final int disks;
    private final transient List<Hanoi.Move> moves;
    private final transient List<Deque<Integer>> pegs =
            List.of(new ArrayDeque<>(), new ArrayDeque<>(), new ArrayDeque<>());
    private int done;               // moves applied so far
    private int lastDisk;           // disk moved by the latest move, or 0

    public HanoiAnimation(int disks) {
        this.disks = disks;
        this.moves = Hanoi.moves(disks, 'A', 'C', 'B');
        for (int d = disks; d >= 1; d--) {
            pegs.get(0).push(d);    // push puts the smaller disk on top
        }
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(BACKGROUND);
    }

    /** Applies the next move; returns false when the puzzle is finished. */
    public boolean step() {
        if (done == moves.size()) {
            return false;
        }
        Hanoi.Move m = moves.get(done++);
        Deque<Integer> from = pegs.get(m.from() - 'A');
        Deque<Integer> to = pegs.get(m.to() - 'A');
        Integer top = from.peek();
        if (top == null || top != m.disk() || (!to.isEmpty() && to.peek() < m.disk())) {
            throw new IllegalStateException("illegal move " + done + ": " + m);
        }
        to.push(from.pop());
        lastDisk = m.disk();
        repaint();
        return true;
    }

    /** True when every disk is on peg C, largest at the bottom. */
    public boolean solved() {
        return pegs.get(0).isEmpty() && pegs.get(1).isEmpty()
                && pegs.get(2).size() == disks;
    }

    public int movesDone() {
        return done;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int baseY = HEIGHT - 50;
        int pegSpacing = WIDTH / 3;
        int maxWidth = pegSpacing - 30;
        int diskHeight = Math.min(22, (baseY - 70) / Math.max(disks, 1));

        g2.setColor(STRUCTURE);
        g2.fillRect(20, baseY, WIDTH - 40, 8);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        for (int p = 0; p < 3; p++) {
            int cx = pegSpacing * p + pegSpacing / 2;
            g2.setColor(STRUCTURE);
            g2.fillRect(cx - 4, 60, 8, baseY - 60);
            g2.setColor(TEXT);
            g2.drawString(String.valueOf((char) ('A' + p)), cx - 5, baseY + 32);

            // Draw bottom to top: the deque's head is the top disk.
            Integer[] stack = pegs.get(p).toArray(new Integer[0]);
            for (int i = 0; i < stack.length; i++) {
                int disk = stack[stack.length - 1 - i];
                int w = 30 + (maxWidth - 30) * disk / Math.max(disks, 1);
                int y = baseY - (i + 1) * diskHeight;
                g2.setColor(disk == lastDisk && i == stack.length - 1 ? MOVED : DISK);
                g2.fillRoundRect(cx - w / 2, y + 1, w, diskHeight - 2, 10, 10);
                g2.setColor(BACKGROUND);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(cx - w / 2, y + 1, w, diskHeight - 2, 10, 10);
            }
        }

        g2.setColor(TEXT);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        g2.drawString("Move " + done + " of " + moves.size(), 20, 30);
    }

    /** Renders the panel into a PNG; works without a display. */
    public void writePng(File file) throws IOException {
        setSize(WIDTH, HEIGHT);
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            paint(g);
        } finally {
            g.dispose();
        }
        if (!ImageIO.write(image, "png", file)) {
            throw new IOException("no PNG writer available");
        }
    }

    private static int parseDisks(String text) {
        int n = Integer.parseInt(text);
        if (n < 1 || n > 10) {
            throw new IllegalArgumentException("disks must be 1 to 10, got " + n);
        }
        return n;
    }

    public static void main(String[] args) throws IOException {
        int n = args.length > 0 ? parseDisks(args[0]) : 5;

        if (args.length >= 3 && args[1].equals("--snapshot")) {
            HanoiAnimation panel = new HanoiAnimation(n);
            int k = args.length > 3 ? Integer.parseInt(args[3]) : Integer.MAX_VALUE;
            while (panel.movesDone() < k && panel.step()) {
                // apply moves up to k
            }
            panel.writePng(new File(args[2]));
            System.out.println("wrote " + args[2] + " after " + panel.movesDone()
                    + " moves; solved: " + panel.solved());
            return;
        }

        SwingUtilities.invokeLater(() -> {
            HanoiAnimation panel = new HanoiAnimation(n);
            JFrame frame = new JFrame("Towers of Hanoi: " + n + " disks");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            Timer timer = new Timer(400, null);
            timer.addActionListener(e -> {
                if (!panel.step()) {
                    timer.stop();
                }
            });
            timer.start();
        });
    }
}
