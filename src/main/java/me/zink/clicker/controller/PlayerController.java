package me.zink.clicker.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import me.zink.clicker.data.Data;
import me.zink.clicker.model.Player;
import me.zink.clicker.repo.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/players")
public class PlayerController {

    @Autowired
    PlayerRepo repo;

    @ResponseBody
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Player> getAllPlayers(){
        try{
            return repo.findAll();
        }catch (Exception ignored){
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    private Player getPlayer(@RequestParam Long id){
        try{
            return repo.getReferenceById(id);
        }catch (Exception ignored){
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
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
}
