package me.zink.clicker.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.zink.clicker.data.Data;
import me.zink.clicker.model.Player;
import me.zink.clicker.repo.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PlayerController {

    @Autowired
    PlayerRepo repo;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public List<Player> getAllPlayers(){
        try{
            return repo.findAll();
        }catch (Exception ignored){
            return null;
        }
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Player getPlayer(@PathVariable long id){
        System.out.println("Path - "+id);
        try{
            return repo.getReferenceById(id);
        }catch (Exception ignored){
            return null;
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String savePlayer(@RequestBody Player player){
        try{
            if(repo.existsById(player.getId())){
               return "Player with this id already exists!";
            }
            repo.save(player);
            return "Saved successfully!";
        }catch (Exception ignored){
            return "Error on saving!";
        }
    }

    //TODO Save by id
    /*@RequestMapping(value = "/save", method = RequestMethod.POST)
    public String savePlayer(@RequestBody long id){
        try{
            Player player = repo.getReferenceById(id);
            return player.toString();
        }catch (Exception ignored){
            return "Player not found!";
        }
    }*/
}
