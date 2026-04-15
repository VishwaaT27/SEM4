import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class DBApp 
{
    static Connection conn;
    public static void connectDB() 
    {
        try 
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521/XEPDB1",
                    "DBSLAB",
                    "7116"
            );
        } 
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
        }
    }
    public static void showLogin() 
    {
        JFrame frame = new JFrame("Login");
        frame.setSize(450, 250);
        frame.setLayout(new GridLayout(3, 2, 10, 10));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        frame.add(new JLabel("Username:"));
        frame.add(userField);
        frame.add(new JLabel("Password:"));
        frame.add(passField);
        frame.add(new JLabel(""));
        frame.add(loginBtn);
        loginBtn.addActionListener(e -> 
        {
            if (userField.getText().equals("admin") && new String(passField.getPassword()).equals("12345")) 
            {
                frame.dispose();
                showAdmin();
            } 
            else JOptionPane.showMessageDialog(frame, "Invalid Login");
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static void showAdmin() 
    {
        JFrame frame = new JFrame("Admin Panel");
        frame.setSize(550, 500);
        frame.setLayout(new GridLayout(4, 2, 10, 10));
        JButton btn1 = new JButton("View All Table Names");
        JButton btn2 = new JButton("View Table Info");
        JButton btn3 = new JButton("View Table Data");
        JButton btn4 = new JButton("Insert Into Table");
        JButton btn5 = new JButton("Delete From Table");
        JButton btn6 = new JButton("Update Table");
        JButton btn7 = new JButton("View Player Rewards (Join)");
        JButton btn8 = new JButton("Back to Login");
        frame.add(btn1);
        frame.add(btn2);
        frame.add(btn3);
        frame.add(btn4);
        frame.add(btn5);
        frame.add(btn6);
        frame.add(btn7);
        frame.add(btn8);
        btn1.addActionListener(e -> viewTables());
        btn2.addActionListener(e -> viewTableInfo());
        btn3.addActionListener(e -> viewTableData());
        btn4.addActionListener(e -> insertData());
        btn5.addActionListener(e -> deleteMenu());
        btn6.addActionListener(e -> updateData());
        btn7.addActionListener(e -> viewJoinData());
        btn8.addActionListener(e -> { frame.dispose(); showLogin(); });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static String[] getTables() 
    {
        try 
        {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT table_name FROM user_tables");
            Vector<String> list = new Vector<>();
            while (rs.next()) list.add(rs.getString(1));
            return list.toArray(new String[0]);
        } 
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
        }
        return new String[0];
    }
    public static void viewTables() 
    {
        JTextArea area = new JTextArea(String.join("\n", getTables()));
        area.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(600, 400));
        JOptionPane.showMessageDialog(null, scroll);
    }
    public static void viewTableInfo() 
    {
        String table = (String) JOptionPane.showInputDialog(null, "Select Table",
                "Tables", JOptionPane.QUESTION_MESSAGE, null, getTables(), null);
        if (table == null) return;
        try 
        {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT column_name, data_type, nullable FROM user_tab_columns WHERE table_name='" + table + "' ORDER BY column_id");
            StringBuilder sb = new StringBuilder();
            while (rs.next()) 
            {
                sb.append(rs.getString(1)).append("\t")
                        .append(rs.getString(2)).append("\t")
                        .append(rs.getString(3)).append("\n");
            }
            JTextArea area = new JTextArea(sb.toString());
            area.setFont(new Font("Monospaced", Font.PLAIN, 16));
            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(700, 500));
            JOptionPane.showMessageDialog(null, scroll);
        } 
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
        }
    }
    public static void viewTableData() 
    {
        String table = (String) JOptionPane.showInputDialog(null, "Select Table",
                "Tables", JOptionPane.QUESTION_MESSAGE, null, getTables(), null);
        if (table == null) return;
        try 
        {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + table);
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            DefaultTableModel model = new DefaultTableModel();
            for (int i = 1; i <= cols; i++)
                model.addColumn(md.getColumnName(i));
            while (rs.next()) 
            {
                Vector<String> row = new Vector<>();
                for (int i = 1; i <= cols; i++) row.add(rs.getString(i));
                model.addRow(row);
            }
            JTable tableUI = new JTable(model);
            tableUI.setFont(new Font("Monospaced", Font.PLAIN, 16));
            tableUI.setRowHeight(28);
            JFrame f = new JFrame("Data: " + table);
            f.add(new JScrollPane(tableUI));
            f.setSize(1100, 600); // 🔥 BIG
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        } 
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
        }
    }
    public static void insertData() 
    {
        String table = (String) JOptionPane.showInputDialog(null, "Select Table",
                "Insert", JOptionPane.QUESTION_MESSAGE, null, getTables(), null);
        if (table == null) return;
        try 
        {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT column_name, data_type FROM user_tab_columns WHERE table_name='" + table + "' ORDER BY column_id");
            Vector<String> cols = new Vector<>();
            Vector<String> types = new Vector<>();
            while (rs.next()) 
            {
                cols.add(rs.getString(1));
                types.add(rs.getString(2));
            }
            JPanel panel = new JPanel(new GridLayout(cols.size(), 2, 10, 10));
            JTextField[] fields = new JTextField[cols.size()];
            for (int i = 0; i < cols.size(); i++) 
            {
                String example = "";
                if (types.get(i).equals("NUMBER")) example = "e.g., 101";
                else if (types.get(i).equals("VARCHAR2")) example = "e.g., John";
                else if (types.get(i).equals("DATE")) example = "YYYY-MM-DD (e.g., 2000-07-02)";
                JLabel label = new JLabel(cols.get(i) + " (" + types.get(i) + " - " + example + ")");
                fields[i] = new JTextField();
                panel.add(label);
                panel.add(fields[i]);
            }
            JScrollPane scroll = new JScrollPane(panel);
            scroll.setPreferredSize(new Dimension(800, 500)); 
            int result = JOptionPane.showConfirmDialog(null, scroll, "Insert Data", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) 
            {
                StringBuilder q = new StringBuilder("INSERT INTO " + table + " VALUES (");
                for (int i = 0; i < cols.size(); i++) 
                {
                    q.append("?");
                    if (i < cols.size() - 1) q.append(",");
                }
                q.append(")");
                PreparedStatement ps = conn.prepareStatement(q.toString());

                for (int i = 0; i < cols.size(); i++) 
                    if (types.get(i).equals("DATE")) ps.setDate(i + 1, Date.valueOf(fields[i].getText()));
                    else ps.setString(i + 1, fields[i].getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Data inserted successfully!");
            }
        } 
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
        }
    }
    public static void deleteRow() 
    {
        String table = (String) JOptionPane.showInputDialog(null, "Select Table",
                "Delete Row", JOptionPane.QUESTION_MESSAGE, null, getTables(), null);
        if (table == null) return;
        String pk = getPrimaryKey(table);
        while (true)
        {
            try 
            {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT " + pk + " FROM " + table);
                Vector<String> ids = new Vector<>();
                while (rs.next()) ids.add(rs.getString(1));
                if (ids.size() == 0) 
                {
                    JOptionPane.showMessageDialog(null, "No data available to delete");
                    return;
                }
                String selectedID = (String) JOptionPane.showInputDialog(
                        null,
                        "Select " + pk + " to delete",
                        "Delete Row",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        ids.toArray(),
                        null
                );
                if (selectedID == null) return;
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Delete row where " + pk + " = " + selectedID + " ?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
                st.executeUpdate("DELETE FROM " + table + " WHERE " + pk + "=" + selectedID);
                JOptionPane.showMessageDialog(null, "Row deleted successfully!");
                return; // success → exit loop
            } 
            catch (Exception e) 
            {
                JOptionPane.showMessageDialog(null, "Invalid input, try again!");
                System.out.println(e.getMessage());
            }
        }
    }
    public static void deleteMenu() 
    {
        String[] options = {"Delete Table", "Delete Row"};
        int choice = JOptionPane.showOptionDialog(null, "Choose Option", "Delete",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);
        if (choice == 0) deleteTable();
        else if (choice == 1) deleteRow();
    }
    public static void deleteTable() 
    {
        String table = (String) JOptionPane.showInputDialog(null, "Select Table",
                "Delete", JOptionPane.QUESTION_MESSAGE, null, getTables(), null);
        if (table == null) return;
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete table " + table + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) 
        {
            try 
            {
                Statement st = conn.createStatement();
                st.executeUpdate("DROP TABLE " + table);
                JOptionPane.showMessageDialog(null, "Table dropped successfully!");
            } 
            catch (Exception e) 
            {
                System.out.println(e.getMessage());
            }
        }
    }
    public static String getPrimaryKey(String table) 
    {
        try 
        {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT cols.column_name FROM user_constraints cons, user_cons_columns cols " +
                            "WHERE cons.constraint_type='P' AND cons.constraint_name=cols.constraint_name AND cols.table_name='" + table + "'"
            );
            if (rs.next()) return rs.getString(1);
        } 
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static void updateData() 
    {
        String table = (String) JOptionPane.showInputDialog(null,
                "Select Table", "Update",
                JOptionPane.QUESTION_MESSAGE, null,
                getTables(), null);

        if (table == null) return;
        String pk = getPrimaryKey(table);
        try 
        {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT " + pk + " FROM " + table);
            Vector<String> ids = new Vector<>();
            while (rs.next()) ids.add(rs.getString(1));
            String selectedID = (String) JOptionPane.showInputDialog(
                    null,
                    "Select " + pk + " (e.g., " + (ids.size()>0?ids.get(0):"1") + ")",
                    "Select Row",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    ids.toArray(),
                    null
            );
            if (selectedID == null) return;
            rs = st.executeQuery("SELECT * FROM " + table + " WHERE " + pk + "=" + selectedID);
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            rs.next();
            JPanel panel = new JPanel(new GridLayout(cols, 2, 10, 10));
            JTextField[] fields = new JTextField[cols];
            Vector<String> colNames = new Vector<>();
            Vector<String> types = new Vector<>();
            for (int i = 1; i <= cols; i++) 
            {
                colNames.add(md.getColumnName(i));
                types.add(md.getColumnTypeName(i));
                String example = "";
                if (types.get(i-1).equals("NUMBER")) example = "e.g., 101";
                else if (types.get(i-1).contains("CHAR")) example = "e.g., text";
                else if (types.get(i-1).equals("DATE")) example = "YYYY-MM-DD (e.g., 2000-07-02)";
                panel.add(new JLabel(colNames.get(i-1) + " (" + example + ")"));
                JTextField tf = new JTextField(rs.getString(i));
                fields[i-1] = tf;
                panel.add(tf);
            }
            JScrollPane scroll = new JScrollPane(panel);
            scroll.setPreferredSize(new Dimension(900, 600));
            int result = JOptionPane.showConfirmDialog(null, scroll, "Update Row", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) 
            {
                for (JTextField f : fields) 
                    if (f.getText().trim().isEmpty()) 
                    {
                        JOptionPane.showMessageDialog(null, "Fill all the fields");
                        return;
                    }
                StringBuilder q = new StringBuilder("UPDATE " + table + " SET ");
                for (int i = 0; i < cols; i++) 
                {
                    q.append(colNames.get(i)).append("=?");
                    if (i < cols - 1) q.append(",");
                }
                q.append(" WHERE ").append(pk).append("=?");
                PreparedStatement ps = conn.prepareStatement(q.toString());
                try 
                {
                    for (int i = 0; i < cols; i++) 
                        if (types.get(i).equals("DATE"))
                            ps.setDate(i+1, Date.valueOf(fields[i].getText()));
                        else
                            ps.setString(i+1, fields[i].getText());
                    ps.setString(cols+1, selectedID);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Update successful");
                } 
                catch (Exception ex) 
                {
                    JOptionPane.showMessageDialog(null, "Invalid data type entered!");
                    System.out.println(ex.getMessage());
                }
            }
        } 
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
        }
    }
    public static void viewJoinData() 
    {
        try 
        {
            Statement st = conn.createStatement();
            String query = "SELECT p.PLAYER_ID, p.PLAYER_NAME, r.REWARD_ID, r.REWARDED_TIME, r.LOGIN_TIME " +
               "FROM PLAYERS p JOIN REWARD r ON p.PLAYER_ID = r.PLAYER_ID";
            ResultSet rs = st.executeQuery(query);

            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            DefaultTableModel model = new DefaultTableModel();

            for (int i = 1; i <= cols; i++) model.addColumn(md.getColumnName(i));
            while (rs.next()) 
            {
                Vector<String> row = new Vector<>();
                for (int i = 1; i <= cols; i++) row.add(rs.getString(i));
                model.addRow(row);
            }
            JTable tableUI = new JTable(model);
            tableUI.setFont(new Font("Monospaced", Font.PLAIN, 16));
            tableUI.setRowHeight(28);
            JFrame f = new JFrame("Player - Reward Join Data");
            f.add(new JScrollPane(tableUI));
            f.setSize(1000, 600);
            f.setLocationRelativeTo(null);
            f.setVisible(true);

        } 
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Error fetching join data");
        }
    }
    public static void main(String[] args) 
    {
        connectDB();
        showLogin();
    }
}