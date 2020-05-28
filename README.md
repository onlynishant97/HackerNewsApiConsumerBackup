# HackerNewsApiConsumer

Use the Hacker News API available at https://github.com/HackerNews/API to build the API backend for a mobile client. The client needs the following endpoints to render its UI:
/top-stories — returns the top 10 stories ranked by score in the last 10 minutes (see instructions). Each story will have the title, url, score, time of submission, and the user who submitted it.
/comments — returns the top 10 parent comments on a given story, sorted by the total number of comments (including child comments) per thread. Each comment will have the comment's text, the user’s HN handle, and their HN age. The HN age of a user is basically how old their Hacker News profile is in years.
/past-stories — returns all the past top stories that were served previously.
Instructions:
To prevent overloading the HN APIs — and from getting your server rate-limited and blocked by Firebase — implement appropriate caching so that all clients connecting to your server will see the same cached data for up to 10 minutes.
You are free to use any appropriate datastores and asynchronous messaging systems to solve this problem.
Solution will be evaluated for its code quality, test coverage, architecture and performance.
Brownie points for setting up your project to run inside Docker.