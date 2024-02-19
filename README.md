














<h1 align="center">
    <img src="https://www.iths.se/wp-content/uploads/2016/02/thumbnails/ithslogoliggandepayoffrgb-4601-1280x450.png" height="130" alt="ITHS">
</h1>

<section>
<div align="center">
    <a href="https://github.com/fungover/haze/actions/workflows/maven.yml">
        <img src="https://github.com/fungover/haze/actions/workflows/maven.yml/badge.svg" alt="Java CI with Maven workflow badge"/>
    </a>
    <a href="https://github.com/fungover/haze/actions/workflows/release-drafter.yml">
        <img src="https://github.com/fungover/haze/actions/workflows/release-drafter.yml/badge.svg" alt="Release Drafter workflow badge"/>
    </a>
</div>
<div align="center">
    <a href="https://github.com/fungover/haze/issues">
        <img src="https://img.shields.io/github/issues-raw/fungover/haze" alt="Open issues workflow badge"/>
    </a>
    <a href="https://github.com/fungover/haze/pulls">
        <img src="https://img.shields.io/github/issues-pr/fungover/haze" alt="Pull request workflow badge"/>
    </a>
    <a href="https://github.com/fungover/haze/issues?q=is%3Aissue+is%3Aclosed">
        <img src="https://img.shields.io/github/issues-closed-raw/fungover/haze" alt="Open issues workflow badge"/>
    </a>
</div>
<div align="center">
    <a href="https://github.com/fungover/haze/releases">
        <img src="https://img.shields.io/github/v/release/fungover/haze?display_name=tag&sort=semver" alt="Release workflow badge"/>
    </a>
    <a href="https://github.com/fungover/haze/pulse">
        <img src="https://img.shields.io/github/commit-activity/m/fungover/haze" alt="Commit activity workflow badge"/>
    </a>
</div>
<div align="center">
    <a href="https://github.com/fungover/haze/graphs/contributors">
        <img src="https://img.shields.io/github/contributors/fungover/haze" alt="Contributors workflow badge"/>
    </a>
    <img src="https://img.shields.io/github/languages/top/fungover/haze" alt="Language workflow badge"/>
</div>
</section>


# Haze:
Key-Value database that talks RESP

## Topics
java redis docker-container maven-pom sonarqube sonarcloud log4j2 html

## Instructions for Haze:
      
    
 1. - Open the command prompt and type in the following:  
     ```docker pull fungover/haze```(This downloads the Docker image)

 2.    - Start a new container with the image, mapping the port 6379 on the host to port 6379 on the container:
     ```docker run -p 6379:6379 fungover/haze```

    
   -   You can change the behaviour of the server by putting in environment variables when starting the container. For example, you can set the server port to 6440. To do this:
    ``docker run -p 6440:6440 -e HAZE_PORT=6440 fungover/haze``.


## Commands:

       Commands that the Server support:
      
 
  - SET:
     Sets a value to a key in Redis.
- GET:
    Retrieves a value to a key in Redis.
- DEL:
    Removes the specified keys.
- PING:
    Tests if the Redis server is running. If the server is running, it returns "PONG".
- SETNX:
    Set the value of a key, only if the key does not already exists.
- EXISTS:
Returns if key exists, it returns 1 if the key exists, and 0 otherwise.
- SAVE:
   Saves the current state of the dataset. It is helpful if you are creating backups or snapshots.
- RPUSH:
  Appends one or multiple elements to the end of a list.
- LPUSH:
  Appends one or multiple elements to the beginning of a list.
- LPOP:
  Removes and returns the first element of the list stored at the key.
- RPOP:
  Removes and returns the last element of the list stored at the key.
- LLEN:
  Returns the length of the list stored at the key.
- LMOVE:
  Removes the first or last element from the source list and pushes it to the destination list.
- LTRIM:
  Trim a list so that it contains only the specified range of elements.
- AUTH:
  Authenticate to the server.
- LINDEX:
  Returns the element at index position in the list stored at the key.
- INCR:
  Increments the number stored at key by one.
- DECR:
  Decrements the number stored at key by one.
   

## Examples:
  1. ```SET key1 "Hi"```
  2. ```GET key1```
  3. ```DEL key1```
  4. ```PING "PONG"```
  5. ```SETNX mykey "Hello"```
  6. ```EXISTS key1```
  7. ```SAVE```
  8. ```RPUSH mylist "a" "b" "c"```
  9. ```LPUSH mylist "d" "e"```
  10. ```LPOP mylist```
  11. ```RPOP mylist```
  12. ```LLEN mylist```
  13. ```LMOVE sourcelist destinationlist LEFT RIGHT```
  14. ```LTRIM mylist 0 1```
  15. ```AUTH password```
  16. ```LINDEX mylist index```
  17. ```INCR key```
  18. ```DECR key```  
  
