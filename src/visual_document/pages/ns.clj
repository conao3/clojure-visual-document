(ns visual-document.pages.ns 
  (:require
   [clojure.string :as str]
   [clj-yaml.core :as yaml]))

(defn pages [[key_ val_]]
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
