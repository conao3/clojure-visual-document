(ns visual-document.pages.index
  (:require
   [visual-document.resources.core :as c.resources.core]
   [hiccup2.core :as hiccup]))

(defn page []
  {:title "index"
   :body (hiccup/html
          [:ul
           (map (fn [x] [:li (:name x)])
                c.resources.core/clojure-core-meta)])})
