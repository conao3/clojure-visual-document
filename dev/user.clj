(ns user
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [babashka.fs :as fs]))

(alter-var-root #'*warn-on-reflection* (constantly true))

(defmacro trap [& body]
  `(try
     (do
       ~@body)
     (catch Exception _# nil)))

(defn ensure-dirs [filepath]
  (-> filepath
      fs/file
      fs/parent
      fs/create-dirs)
  filepath)

(comment
  (def clojure-core-meta-raw
    (->> (ns-publics 'clojure.core)
         (sort-by (comp name key))
         (map (comp meta val))))

  (-> clojure-core-meta-raw
      (json/write-str
       :indent 2
       :value-fn (fn [_ val]
                   (let [c (class val)]
                     (cond
                       (= c clojure.lang.Namespace) (str val)
                       (= c Class) (str val)
                       (= c clojure.lang.Var) (str val)
                       (and (fn? val) (nil? (trap (name val)))) "<lambda>"
                       :else val))))
      (->> (spit (ensure-dirs "src/json/clojure.core.json"))))

  (def clojure-core-meta
    (-> "src/json/clojure.core.json"
        fs/file
        io/reader
        json/read-json)))
