package fleet.organization;
import javax.swing.*;
import java.awt.*;

public class FleetOptions extends JFrame{
    JTabbedPane tabs = new JTabbedPane();
    public FleetOptions(String role, int userId){
        setTitle("Fleet Management System");
        setSize(900,600);
        setLayout(new BorderLayout());

        if (role.equalsIgnoreCase("admin")){
            tabs.add("users", new UserPanel());

        }
        else {
            tabs.add("Fleets", new OrganizationPanel(userId));
        }
        setVisible(true);
        add(tabs,BorderLayout.CENTER);
    }
}
