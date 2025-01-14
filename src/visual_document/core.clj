(ns visual-document.core
  (:require
   [clojure.tools.logging :as log]
   [hiccup.page :as hiccup.page]
   [hiccup2.core :as hiccup]
   [stasis.core :as stasis]
   [ring.adapter.jetty :as jetty]
   [visual-document.pages.index :as c.pages.index])
  (:gen-class))

(def blog-name "clojure.core cheatsheet")
(def stasis-config {:stasis/ignore-nil-pages? true})

(defn render-page [{:keys [title body]}]
  (hiccup.page/html5 {:lang "ja"}
   [:head
    [:meta {:http-equiv "Content-Type" :content "text/html; charset=utf-8"}]
    [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
    [:title (format "%s - %s" title blog-name)]
    (hiccup.page/include-css "/assets/modern-css-reset.css")
    (hiccup.page/include-css "/assets/spectrum/color-palette.css")
    (hiccup.page/include-css "/assets/spectrum/typography.css")
    (hiccup.page/include-css "/assets/index.css")]
   [:body
    [:h2 blog-name]
    [:div {:style {:display "flex"}}
     [:a {:href "/"} "Home"]]
    (hiccup/raw body)]))

(defn site []
  (stasis/merge-page-sources
   {:public (stasis/slurp-directory "resources/public" #"\.[^.]+$")
    :spectrum (-> (stasis/slurp-directory "generated/spectrum" #"\.[^.]+$")
                  (update-keys (partial str "/assets/spectrum")))
    :static {"/index.html" (render-page (c.pages.index/page))}}))

(defn start-server [& _args]
  (jetty/run-jetty
    (stasis/serve-pages site stasis-config)
    {:port 8080 :join? false}))

(defn -main
  "The entrypoint."
  [& args]
  (log/info args))
