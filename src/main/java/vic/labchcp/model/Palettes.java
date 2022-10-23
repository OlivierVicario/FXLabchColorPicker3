package vic.labchcp.model;

import java.util.ArrayList;

public class Palettes {
   public  ArrayList<Palette> items;

    public ArrayList<Palette> getItems() {
        return items;
    }

    public void setItems(ArrayList<Palette> items) {
        this.items = items;
    }

    public Palettes(){
       items = new ArrayList<Palette>();
   }

}
