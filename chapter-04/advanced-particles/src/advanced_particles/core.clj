(ns advanced-particles.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-particles.records.vector2d :refer :all]
            [basic-particles.protocols.vector :as v]))

(defn get-component
  ([entity component-name] (get-component entity component-name nil))
  ([entity component-name default-value]
    (let [components (second (first entity))
          found (component-name (first (filter (fn [c] (contains? c component-name)) components)))]
      (if (nil? found)
        default-value
        found))))

(defn render-particle [e]
  (let [pos (get-component e :position)
        lifespan (get-component e :lifespan)]
    (q/fill 20 lifespan)
    (q/ellipse (:x pos) (:y pos) 20 20)))

(defn renderer [renderables]
  (doall (map (fn [e] 
                (let [render (get-component e :renderable)]
                  (render e)
                  e))
              renderables)))

(defn particle []
  {:particle [{:position (->Vector2D 200 200)}
              {:acceleration (->Vector2D 0 0)}
              {:mass 1}
              {:velocity (->Vector2D 5 -2)}
              {:lifespan 255}
              {:renderable render-particle}]})

(defn setup []
  (q/frame-rate 60)
  {:entities [(particle)]})

(defn run [{entities :entities}]
  (let [updated-entities (-> entities
                           (renderer))]
    {:entities updated-entities}))

(defn -main []
  (q/defsketch basic-particles
    :title "Advanced Particles"
    :size [640 480]
    :setup setup
    :update run
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
