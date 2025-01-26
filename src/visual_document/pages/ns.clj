(ns visual-document.pages.ns 
  (:require
   [clojure.string :as str]
   [clj-yaml.core :as yaml]))

(defn page [[key_ val_]]
  (let [key (-> key_
                (str/replace #"\.yml$" "/index.html")
                (->> (str "/ns")))
        val {:title (str/replace key_ #"\.yml$" "")
             :body [:ul
                    (->> val_
                         yaml/parse-string
                         (map (fn [[k v]]
                                [:li (str k) (str v)])))]}]
    [key val]))

(defn pages [obj]
  (->> obj
       (map page)
       (into {})))
