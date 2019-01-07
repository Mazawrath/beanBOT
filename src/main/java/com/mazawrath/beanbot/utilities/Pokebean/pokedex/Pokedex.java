//package com.mazawrath.beanbot.utilities.Pokebean.pokedex;
//
//import com.mazawrath.beanbot.utilities.Pokebean.Monster.Pokebean;
//import com.mazawrath.beanbot.utilities.Pokebean.Monster.Type;
//
//import java.awt.*;
//import java.util.ArrayList;
//
//
//public class Pokedex {
//    private List<Pokebean> list = new ArrayList<Pokebean>();
//
//    /**
//     * Creates a new Pokedex object and uses the loadListFromSave method to load data from the save file specified.
//     * @param save The text file to load from.
//     */
//    public Pokedex(final String save) {
//        loadListFromSave(save);
//    }
//
//    /**
//     * Loads data from the save file specified.
//     * @param save The text file to load from.
//     */
//    public void loadListFromSave(final String save) {
//        //TODO - see below TODO
//    }
//
//    /**
//     * A method that runs at the start of any specific event where there is a possibility of a new entry.
//     */
//    public void store() {
//        //TODO - no idea how the fuck to do this without other classes getting finished.
//    }
//
//    /**
//     * Searches for Pokebean in the pokedex by name.
//     * @param name The name of Pokebean to search.
//     * @return The Pokebean returned.
//     */
//    public List<Pokebean> searchByName(final String name) {
//        List<Pokebean> found = new ArrayList<Pokebean>();
//
//        for(final Pokebean p : this.list) {
//            if(p.getName().contains(name)) {
//                found.add(p);
//            }
//        }
//        return found;
//    }
//
//    /**
//     * Searches for Pokebean in the pokedex by type.
//     * @param type The type of Pokebean to search.
//     * @return The Pokebean returned.
//     */
//    public List<Pokebean> searchByType(final Type type) {
//        List<Pokebean> found = new ArrayList<Pokebean>();
//
//        for(final Pokebean p : this.list) {
//            for(final Type t : p.getType()) {
//                if(t == type) {
//                    found.add(p);
//                    break;
//                }
//            }
//        }
//        return found;
//    }
//
//    /**
//     * Gets the sprite image for the Pokebean specified.
//     * @param p The Pokebean to get the sprite of.
//     * @return The image of the sprite.
//     */
//    public Image displaySprite(final Pokebean p) {
//        return null;
//    }
//}
