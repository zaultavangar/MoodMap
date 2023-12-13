# 🌎 MoodMap 😍

## ✨ Project Details

**MoodMap** is a web app that let's you interactively view news sentiment trends over time on a map. Our database contains articles the Guardian dating back to January 2019, and is updated each day with new articles. 

Maps was built using [React](https://react.dev/) and [TypeScript](https://www.typescriptlang.org/).

News data was sourced from the [Guardian Open Platform](https://open-platform.theguardian.com/).

## 💻 Installation

To install MoodMap, you need to clone the repo from [GitHub](https://github.com/). Run the following commands in your terminal:

```shell
git clone https://github.com/zaultavangar/MoodMap.git
cd MoodMap
```

## 🏃🏾‍♂️💨 Running MoodMap Server

To run the MoodMap server locally, run the following command in your terminal:

```shell
cd backend
npm run start
```

- TODO: EXPLAIN HOW TO INSTALL RABBITMQ AS WELL (FOR BOTH MAC AND WINDOWS), AS THE BACKEDN MAY NOT WORK WITHOUT IT 

## 🏃🏾‍♂️💨 Running MoodMap Website

To run the MoodMap website locally, run the following command in your terminal:

```shell
cd frontend
npm run dev
```


## 🔬 Running Tests

### 🧪 Running Unit Tests

MoodMap uses [Vitest](https://jestjs.io/) for unit tests.

#### Frontend

To run the unit tests on the frontend, run the following command in your terminal:

```shell
cd frontend
npm run test:unit
```

### 🧪 Running End-to-End Tests

MoodMap uses [Playwright](https://playwright.dev/) for end-to-end tests.

To run the end-to-end tests, run the following command in your terminal:

```shell
cd frontend
npm run test:e2e
```


## Overview and Project Details 

### Database Structure
We selected MongoDB as our database solution mainly due to its proficiency in handling complex, nested, and JSON-like data structures. Our database comprises two primary collections: 'Articles' and 'Features.' The 'Articles' collection archives the content fetched from the Guardian, encompassing a variety of elements such as titles, URLs, thumbnails, and textual content. Conversely, the 'Features' collection is dedicated to storing GeoJson features, which primarily represent geographical locations.

The structure of our database entities is outlined below. For a detailed implementation, refer to the [ArticleEntity](./backend/src/main/java/com/example/backend/entity/ArticleEntity.java) and [FeatureEntity](./backend/src/main/java/com/example/backend/entity/FeatureEntity.java).

- Article
  - ObjectId _id (e.g. "507f1f77bcf86cd799439011")
  - LocalDateTime webPublicationDate (e.g. "2023-11-29 13:56:57.674")
  - String webTitle 
  - String webUrl
  - String thumbnail
  - String bodyText
  - Double sentimentScore (e.g. 0.35)
  - List<String<st>> associatedLocations (e.g. ["France", "Germany"])
- Feature
  - ObjectId _id (e.g. "507f1f77bcf86cd799439011")
  - String type (always equal to "Feature")
  - String location (e.g. "France")
  - [GeoJsonGeometry](./backend/src/main/java/com/example/backend/geocodingService/GeoJsonGeometry.java) geoJsonGeometry
  - Map<String,Object> properties (a map of strings to objects)

A key aspect of the 'Feature' collection is the properties field, which plays a pivotal role in the frontend visualization, such as determining the size and color of a feature's circle. This field stores the average sentiment of the articles mentioning a particular location for each month/year, thereby enriching our data representation on the frontend.


### Design Choices and Project Structure

#### Using Spring/Spring Boot
We decided to use Spring as our foundational framework primarily for its strong capabilities in crafting robust and well-documented REST APIs, managing database interactions, supporting depedency injection, and facilitating message brokering with tools like RabbitMQ. Perhaps most importantly, Spring Boot's approach to dependency injection, which allows components to be dynamically injected at runtime, not only streamlines the management of application dependencies but also promotes a modular architecture. This modular design is further enhanced by Spring's testing support, making it effortless to inject mock implementations for unit testing, thereby ensuring reliable and maintainable code. Spring simplifies the integration with various database technologies, offering a straightforward approach for managing database connections, executing queries, and implementing transactions. Using Spring Data JPA, we were able to greatly simplify the interaction with our MongoDB database. Furthermore, Spring's support for RabbitMQ is seamless. It provides abstractions for message-driven beans, allowing us to easily implement reliable messaging and asynchronous processing. This was particularly beneficial in the preprocesing of the articles, allowing us to decouple the addition and updating of articles and features in our database. 

#### Project Structure 
- TODO: EXPLAIN THE MODULES/DIRECTORIES AND HOW THEY INTERACT 

### Preprocessing 
Much of the work on the backend was the preprocessing of the Guardian articles. Articles were fetched via the [Guardian API](https://open-platform.theguardian.com/). After performing natural language processing and geocoding (explained in more detail below), the articles and their associated locations (structured very similarly to GeoJSON features) were added to the Article and Feature collections in our MongoDB database, respectively. 

#### Natural Language Processing (NLP)

##### Named Entity Recognition (NER)
To compensate for the lack of geographic data in the Guardian API, we employed Named Entity Recognition (NER) using Stanford NLP's [Simple API](https://stanfordnlp.github.io/CoreNLP/simple.html) on each article's headline. This method allowed us to pinpoint and capture location-based entities, effectively connecting articles to specific geographical areas. The API analyzes each headline and assigns entity tags (like PERSON, LOCATION, etc.) to words. Words tagged as locations – encompassing countries, states, cities, or provinces – were cataloged. Moreover, nationalities identified by the API were converted into corresponding locations using an online nationality-to-country mapping. These identified locations then populated the 'associated_locations' field in our Article document and were further utilized in populating our database's Feature collection, as detailed in the [Geocoding section](#forward-geocoding).

#### Sentiment Analysis 
We utilized Hugging Face's API, specifically the [Bert Base Multilingual Uncased Sentiment Model](https://huggingface.co/nlptown/bert-base-multilingual-uncased-sentiment), to assign sentiment scores to articles. The model evaluates both the headline and body text (limited to 512 characters) and provides a structured response, as depicted below.

![Alt text](./readme_images/sample_sentiment_api_output.png)

The model assigns a sentiment score ranging from 1 (most negative) to 5 (most positive), accompanied by corresponding probabilities. We calculated a normalized weighted average sentiment score using the formula below, with the score ranging from 0 (most negative) to 1 (most positive). This score was then linked to the respective article in our database.

$$\[ \text{Normalized Weighted Average} = \frac{ \sum (\text{star}_i \times \text{score}_i) - 1}{4} \]$$

$$\text{where} \; \text{star}_i = \text{First character of label of the ith sentiment converted to an integer}$$

$$\text{and} \; \text{score}_i = \text{Score of the ith sentiment}$$


### Forward Geocoding 
Following the location association outlined in the [NER section](#named-entity-recognition-ner), we aimed to pinpoint the exact coordinates of each location using the [Google Maps Geocoding API](https://rapidapi.com/googlecloud/api/google-maps-geocoding) via [Rapid API](https://rapidapi.com/hub). Initially, we tried Mapbox's Geocoding API but switched due to inconsistencies in its results. Each location linked to an article prompted an API call. The response included a [GeoJson Point](https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.2) with coordinates and a formatted location name. Successful extractions led to the addition of these features into our database's Feature collection.

### Processing Technique
For efficiency and cost considerations, we segmented our processing into 10 distinct batches. Each year's data retrieval involved two API calls: one for articles from June 1st to December 31st, and another for January 1st to May 31st. This approach, spanning five years (2019-2023), necessitated a total of 10 API calls to the Guardian. Typically, each call yielded approximately 6,000 to 7,000 articles. Given the extensive volume of articles and the complexity involved in processing them, our goal was to maximize efficiency and ensure robust error handling. To achieve this, we employed [RabbitMQ](https://www.rabbitmq.com/), an online message broker, which primarily facilitated the segregation of article addition and feature updates in our database.

#### Preprocessing with RabbitMQ
In preprocessing past articles, we separated the database operations - specifically the addition and updating of articles and features. Here's an outline of our preprocessing workflow:

- Post retrieval from the Guardian API, each article was initially stored in our database sans sentiment score or associated location list.
- Upon adding an article, our [Processor](./backend/src/main/java/com/example/backend/processors/Processor.java) dispatches a message via RabbitMQ to the article queue, containing the article's database ID.
- The [Preprocessor](./backend/src/main/java/com/example/backend/processors/Preprocessor.java) picks up this ID, extracts the corresponding article from the database, and executes the required processes (Sentiment Analysis, NER, and Geocoding).
- Articles not linked to any location are removed from the database. Conversely, those associated with locations are updated with their respective sentiment scores and location lists.

#### Daily Processing
Our approach for daily processing diverges from the above method, primarily due to the lower volume of articles retrieved daily from the Guardian API (around 30 articles). This smaller scale negated the need for a message broker like RabbitMQ. For daily updates, we directly retrieve articles, conduct NLP tasks and geocoding, and then simultaneously add the articles along with their corresponding features to the database. This process differs from our batch preprocessing by eliminating the initial step of adding and subsequently updating articles in the database. The daily extraction of articles from the Guardian, targeting the current day's publications, is systematically conducted at 5 PM Eastern Standard Time (EST). (SUBJECT TO CHANGE).

#### Average Sentiment Calculation 
You may wonder how we manage to showcase the average sentiment of articles that reference a specific location, displayed by month or year, on our frontend. This process is streamlined as follows: each time a new article is added to our database (in the case of preprocessing), or fetched from the Guardian API (in the case of daily processing), we simultaneously update the 'properties' section within the relevant feature(s). These 'properties' act as a storage map, cataloging not only the cumulative number of articles mentioning the location for a given month but also the collective average sentiment score derived from these articles. As we add more articles, the corresponding feature's 'properties' are dynamically adjusted, ensuring that both the article count and the average sentiment for that month are kept up-to-date.

### API Endpoints
- TODO: STATE THE 3 USED API ENDPOINTS
- TODO: EXPLAN
- TODO: EXPLAIN HOW TO VIEW GENERATED SPRING DOC FOR THE ENDPOINTS, WHICH WILL EXPLAIN THEM IN FURTHER DETAIL

## Backend Tests (unit, integration, random)
- TODO: OUTLINE BACKEND TEST SUITES

## Frontend Tests (unit, integration, random, e2e)

## 🥵 Contributors

Made with 💖 by Nico, Zaul, and Asher
