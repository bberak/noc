(defproject steering "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [quil "2.4.0"]
                 [org.nfrac/cljbox2d "0.5.0"]
                 [org.nfrac/cljbox2d.testbed "0.5.0"]
                 [org.toxiclibs/toxiclibs-clj "0.2.0-SNAPSHOT"]]
  :resource-paths ["resources/toxiclibs-complete-0020/verletphysics/library/verletphysics.jar"
                   "resources/toxiclibs-complete-0020/toxiclibscore/library/toxiclibscore.jar"]
  :main steering.core)
