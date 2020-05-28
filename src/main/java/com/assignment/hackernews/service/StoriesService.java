package com.assignment.hackernews.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.assignment.hackernews.pojo.Comment;
import com.assignment.hackernews.pojo.Item;
import com.assignment.hackernews.pojo.Story;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StoriesService {

	@Autowired
	RestTemplate restTemplate;

	public List<Item> getStoryOfLastTenMinutes(List<String> listOfStoryIds) {
		System.out.println(System.currentTimeMillis());
		List<Item> listOfItems = new ArrayList<Item>();
		int count = 0;
		for (String storyId : listOfStoryIds) {
			listOfItems.add(getItemForId(storyId));
		}
		List<Item> listOfStories = listOfItems.stream().filter(item -> item.getType().equalsIgnoreCase("story"))
				.collect(Collectors.toList());

		Comparator<Item> comp = new Comparator<Item>() {
			@Override
			public int compare(Item item1, Item item2) {

				Date date1 = new Date(Long.valueOf(item1.getTime()) * 1000L);
				Date date2 = new Date(Long.valueOf(item2.getTime()) * 1000L);
				return date1.compareTo(date2);
			};
		};
		listOfStories.sort(comp);
		Collections.reverse(listOfStories);
		listOfStories.subList(10, listOfStories.size()).clear();
		// for (Item i : listOfStories) System.out.println(i);

		listOfStories.sort((Item item1, Item item2) -> {
			return Integer.valueOf(item1.getScore()) - Integer.valueOf(item2.getScore());
		});

		listOfStories.stream().forEach(System.out::println);
		return listOfStories;
	}

	private Item getItemForId(String storyId) {
		String jsonResponse = restTemplate
				.getForEntity("https://hacker-news.firebaseio.com/v0/item/" + storyId + ".json", String.class)
				.getBody();
		Item item = null;
		try {
			item = new ObjectMapper().readValue(jsonResponse, Item.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return item;
	}

	public List<Comment> extractCommentsFromStory(Item story) {
		System.out.println("------------------------------------------");
		List<String> kids = story.getKids();
		if (kids.size() > 10)
			kids.subList(10, kids.size()).clear();
		List<Comment> comments = new ArrayList<Comment>();
		for (String id : kids) {
			Comment comment = fetchComment(id);
			comments.add(comment);
		}
		comments.sort((Comment comment1, Comment comment2) -> {
			int size1 = 0, size2 = 0;
			if (comment1.getKids() != null)
				size1 = comment1.getKids().size();
			if (comment2.getKids() != null)
				size2 = comment2.getKids().size();
			return size1 - size2;
		});
		return comments;
	}

	private Comment fetchComment(String storyId) {
		String jsonResponse = restTemplate
				.getForEntity("https://hacker-news.firebaseio.com/v0/item/" + storyId + ".json", String.class)
				.getBody();
		Comment item = null;
		try {
			item = new ObjectMapper().readValue(jsonResponse, Comment.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return item;
	}

}
