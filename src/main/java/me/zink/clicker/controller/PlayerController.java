package me.zink.clicker.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import me.zink.clicker.data.Data;
import me.zink.clicker.model.Player;
import me.zink.clicker.repo.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    private Player getPlayerByID(@RequestParam() Long id){
        try{
            return repo.getReferenceById(id);
        }catch (Exception ignored){
            return null;
        }
    }


    /**
     * It'S EXTREMELY UNSAFE
     */
    /*@ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    private Player getPlayerByName(@PathVariable(value="name") String name){
        System.out.println("Tried to find player");
        try{
            return repo.findAll().stream().filter(player -> player.getName().equals(name)).toList().get(0);
        }catch (Exception ignored){
            System.out.println("Player not found");
            try{
                System.out.println("Name - "+name);
                return getPlayerByID(Long.valueOf(name));
            }catch (Exception ignored2){}
            return null;
        }
    }*/

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
