(ns visual-document.core
  (:require
   [clojure.tools.logging :as log]
   [hiccup.page :as h.page]
   [hiccup.util :as h.util]
   [hiccup2.core :as h]
   [ring.adapter.jetty :as jetty]
   [stasis.core :as stasis]
   [visual-document.pages.function-notation :as c.pages.function-notation]
   [visual-document.pages.index :as c.pages.index]
   [visual-document.pages.ns :as c.pages.ns]
   [visual-document.pages.sitemap :as c.pages.sitemap])
  (:gen-class))

(def blog-name "clojure.core cheatsheet")
(def stasis-config {:stasis/ignore-nil-pages? true})

(defn render-page [{:keys [title body]}]
  (h.page/html5 {:lang "ja"}
   [:head
    [:meta {:http-equiv "Content-Type" :content "text/html; charset=utf-8"}]
    [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
    [:title (format "%s - %s" title blog-name)]
    (h.page/include-css "/assets/modern-css-reset.css")
    (h.page/include-css "/assets/spectrum/color-palette.css")
    (h.page/include-css "/assets/spectrum/typography.css")
    (h.page/include-css "/assets/index.css")]
   [:body
    [:h2 blog-name]
    [:div {:style {:display "flex"}}
     [:a {:href "/"} "Home"]]
    (if (h.util/raw-string? body)
      (h/raw body)
      (h/html body))]))

(defn site []
  (let [obj (stasis/merge-page-sources
            {:public (stasis/slurp-directory "resources/public" #"\.[^.]+$")
             :spectrum (-> (stasis/slurp-directory "generated/spectrum" #"\.[^.]+$")
                           (update-keys (partial str "/assets/spectrum")))
             :prd-ns (-> (->> (stasis/slurp-directory "resources/prd/ns" #"\.yml$")
                              (map c.pages.ns/pages)
                              (into {}))
                         (update-vals render-page))
             :static {"/index.html" (render-page (c.pages.index/page))
                      "/function-notation/index.html" (render-page (c.pages.function-notation/page))}})]
    (-> obj
        (assoc "/sitemap/index.html" (-> (c.pages.sitemap/sitemap-html obj) render-page)))))

(defn export [& _args]
  (let [export-dir "./target"
        load-export-dir #(stasis/slurp-directory export-dir #"\.[^.]+$")
        old-files (load-export-dir)]
    (stasis/empty-directory! export-dir)
    (println "Exporting...")
    (stasis/export-pages (site) "target/" stasis-config)
    (println "Export complete:")
    (stasis/report-differences old-files (load-export-dir))))

(defn start-server [& _args]
  (jetty/run-jetty
    (stasis/serve-pages site stasis-config)
    {:port 8080 :join? false}))

(defn -main
  "The entrypoint."
  [& args]
  (log/info args))
