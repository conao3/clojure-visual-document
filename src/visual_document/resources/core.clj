(ns visual-document.resources.core
  (:require
   [clojure.java.io :as io]
   [clojure.data.json :as json]
   [babashka.fs :as fs]))

(def clojure-core-meta
    (-> "src/json/clojure.core.json"
        fs/file
        io/reader
        json/read-json))
