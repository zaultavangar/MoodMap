import NodeCache from "node-cache";

export abstract class CachingProxy<T> {
  protected cache: NodeCache;

  constructor(stdTTL: number, checkperiod: number, useClones: boolean){
    this.cache = new NodeCache({ stdTTL, checkperiod, useClones});
  }

  abstract handleRequest(...args: any[]): Promise<T>;
}