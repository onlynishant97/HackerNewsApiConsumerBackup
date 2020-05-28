package com.assignment.hackernews.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.assignment.hackernews.pojo.Comment;
import com.assignment.hackernews.pojo.Item;
import com.assignment.hackernews.pojo.Story;
import com.assignment.hackernews.service.StoriesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class StoriesController {
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	StoriesService storiesService;
	
	@Cacheable(value="top-stories")
	@GetMapping("/top-stories")
	public List<Item> getTopTenStories(){
		String topStories = restTemplate.getForEntity("https://hacker-news.firebaseio.com/v0/topstories.json", String.class).getBody();
		List<String> listOfStoryIds =  Arrays.stream(topStories.substring(1,topStories.length()-1).split(",")).collect(Collectors.toList());
		return storiesService.getStoryOfLastTenMinutes(listOfStoryIds);
	}
	
	@Cacheable(value="comments", key="#storyId")
	@GetMapping("/comments/{storyId}")
	public List<Comment> getComments(@PathVariable String storyId){
		String storyJson = restTemplate.getForEntity("https://hacker-news.firebaseio.com/v0/item/"+storyId +".json", String.class).getBody();
		Item story=null;
		try {
			story = new ObjectMapper().readValue(storyJson, Item.class);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return storiesService.extractCommentsFromStory(story);
	}
	
	@GetMapping("/past-stories")
	public List<Story> pastTopStoriesThatWereServerdPreviously(){
		return null;
	}



	@GetMapping("/p/{id}")
	public String createProducts(@PathVariable String id) {
		System.out.println("In");
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<Item> entity = new HttpEntity<Item>(headers);

		String s = restTemplate.exchange("https://hacker-news.firebaseio.com/v0/item/" + id + ".json", HttpMethod.GET,
				entity, String.class).getBody();
		
		try {
			System.out.println(new ObjectMapper().readValue(s, Item.class));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
		
	}



}
