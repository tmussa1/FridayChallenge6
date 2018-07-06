package com.mc.imageranker;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.cloudinary.utils.ObjectUtils.asMap;


@Controller
public class HomeController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    FriendsRepository friendsRepository;

    @Autowired
    CloudinaryConfig cloudinaryConfig;

    @RequestMapping("/")
    public String homePage(){
        return "index";
    }

    @RequestMapping("/register")
    public String registerUser(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String saveUser(@ModelAttribute("user") User user){
        Role role = roleRepository.findByRole("USER");
        user.addRole(role);
        userRepository.save(user);
        return "index";
    }

    @RequestMapping("/addfriends")
    public String addFriends(Model model){
        model.addAttribute("friend", new Friends());
        return "friendsPage";
    }

    @PostMapping("/addfriends")
    public String processFriend(@ModelAttribute("friend") Friends friend, Model model, MultipartHttpServletRequest request) throws IOException{
            MultipartFile fi = request.getFile("file");
        if(fi.isEmpty() && friend.getPicture().isEmpty()){
            return "index";
        }
        try {
            Map uploadResult = cloudinaryConfig.upload(fi.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            String myURL = (String) uploadResult.get("url");
            String uploadName = (String) uploadResult.get("public_id");
            String finalImage = cloudinaryConfig.createUrl(uploadName);
            friend.setPicture(finalImage);
            friendsRepository.save(friend);
        } catch(Exception e){
            e.printStackTrace();
            return "redirect:/addfriends";
        }
        model.addAttribute("friends", friendsRepository.findAllByOrderByRankDesc());
        return "showfriends";
    }
    @RequestMapping("/listfriends")
    public String listFriends(Model model){
        model.addAttribute("friends", friendsRepository.findAllByOrderByRankDesc());
        return "showfriends";
    }

    @PostConstruct
    public void loadRoles(){
        Role role = new Role("USER");
        roleRepository.save(role);
    }

}


