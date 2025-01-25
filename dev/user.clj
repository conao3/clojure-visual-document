(ns user
  (:require
   [babashka.fs :as fs]
   [clj-yaml.core :as yaml]
   [clojure.string :as str]
   [clojure.walk :as walk]))

(alter-var-root #'*warn-on-reflection* (constantly true))

(defmacro trap [& body]
  `(try
     (do
       ~@body)
     (catch Exception _# nil)))

(defn get-ns-map [ns]
  (->> (ns-publics ns)
       (map (juxt val (comp meta val)))
       (into {})))

(defn serialize [val]
  (->> val
       (walk/postwalk (fn [val]
                        (let [c (class val)]
                          (cond
                            (= c clojure.lang.Namespace) (str val)
                            (= c clojure.lang.Var) (str val)
                            (class? val) (.getName ^Class val)
                            (symbol? val) (name val)
                            (and (fn? val) (nil? (trap (name val)))) "<lambda>"
                            :else val))))))

(defn generate-ns-files [ns]
  (-> (get-ns-map ns)
      (update-vals (comp yaml/generate-string serialize))
      (->> (map (fn [[key val]]
                  (let [file (fs/file "resources" "gen" "ns"
                                      (-> key symbol namespace (str/replace "." "/"))
                                      (str
                                       (-> key symbol name
                                           (str/replace "/" "[s]")
                                           (str/replace #"[A-Z]" #(str "_" (str/lower-case %1))))
                                       ".yml"))]
                    (-> file fs/parent fs/create-dirs)
                    (->> val (spit file))))))))

(comment
  (-> (generate-ns-files 'clojure.core) dorun))
