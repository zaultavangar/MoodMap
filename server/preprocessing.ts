import { Processor } from "./Processor";
import { RealSentimentClient } from "./apiClients/sentimentClient/RealSentimentClient";
import { RealNerClient } from "./apiClients/nerClient/RealNerClient";
import { RealGuardianClient } from "./apiClients/guardianClient/RealGuardianClient";
import { RealMapboxClient } from "./apiClients/mapboxClient/RealMapboxClient";
import { CachingGeocodeProxy } from "./caches/CachingGeocodeProxy";

export const preprocessArchives = () => {
  const processor = new Processor(new RealSentimentClient, new RealNerClient,
    new RealGuardianClient, new CachingGeocodeProxy(new RealMapboxClient) )

  // from date and to date 
  let fromDate: string = '';
  let toDate: string = '';
  processor.processArticles(fromDate, toDate);
  /* should add all articles and associated features from 
    fromDate to toDate to their respective tables in the DB */
}

// Call preprocessArchives to population the database as needed
