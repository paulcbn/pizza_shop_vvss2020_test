package pizzashop.repository;

import org.apache.log4j.Logger;
import pizzashop.model.MenuDataModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MenuRepository {
    private static String filename = "data/menu.txt";
    private List<MenuDataModel> listMenu;
    static final Logger logger = Logger.getLogger(MenuRepository.class);

    private void readMenu() {
        ClassLoader classLoader = MenuRepository.class.getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());
        this.listMenu = new ArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                MenuDataModel menuItem = getMenuItem(line);
                listMenu.add(menuItem);
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private MenuDataModel getMenuItem(String line) {
        MenuDataModel item = null;
        if (line == null || line.equals("")) return null;
        StringTokenizer st = new StringTokenizer(line, ",");
        String name = st.nextToken();
        double price = Double.parseDouble(st.nextToken());
        item = new MenuDataModel(name, 0, price);
        return item;
    }

    public List<MenuDataModel> getMenu() {
        readMenu();//create a new menu for each table, on request
        return listMenu;
    }

}
