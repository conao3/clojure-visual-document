(ns visual-document.pages.sitemap 
  (:require
   [clojure.string :as str]))

(defn sitemap-html [obj]
  {:title "sitemap"
   :body [:ul
          (->> obj
               (sort-by key)
               (filter (fn [[key _val]]
                         (str/ends-with? key ".html")))
               (map (fn [[key_ _val]]
                      (let [key (-> key_ (str/replace #"/index\.html$" ""))]
                        [:li [:a {:href key} key]]))))]})
