(set-env!
  :resource-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.7.0"]
                  [adzerk/bootlaces "0.1.13"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.1.0-SNAPSHOT")

(bootlaces! +version+)

(task-options!
  pom {:project     'dgellow/systemd
       :version     +version+
       :description "Wrapper to systemd commands."
       :url         "https://github.com/dgellow/systemd-clj"
       :scm         {:url "https://github.com/dgellow/systemd-clj"}
       :license     {"Eclipse Public License"
                     "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build []
  (comp
   (pom)
   (jar)
   (target)
   (install)))
