(ns advanced-particles.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-particles.records.vector2d :refer :all]
            [basic-particles.protocols.vector :as v]
            [basic-ces.core :refer :all]))

(defn wind [entities]
  (let [entities-with-mass (filter-entities entities :mass :velocity)]
    (map-entities (fn [id comps]
           (let [mass (:mass comps)
                 wind (->Vector2D -0.2 0)
                 acceleration (v/divide wind mass)]
            {id (update comps :velocity v/add acceleration)}))
         entities-with-mass)))

(defn gravity [entities]
  (let [entities-with-mass (filter-entities entities :mass :velocity)]
    (map-entities (fn [id comps]
           (let [mass (:mass comps)
                 gravity (->Vector2D 0 0.51)
                 acceleration (v/divide gravity mass)]
            {id (update comps :velocity v/add acceleration)}))
         entities-with-mass)))

(defn mover [entities]
  (let [movers (filter-entities entities :position :velocity)]
    (map-entities (fn [id comps] 
           {id (update comps :position v/add (:velocity comps))})
         movers)))

(defn degeneration [entities]
  (let [degenerative-entities (filter-entities entities :lifespan)]
    (map-entities (fn [id comps]
           (let [new-lifespan (- (:lifespan comps) 2)]
             (if (< new-lifespan 0)
              {}
              {id (assoc comps :lifespan new-lifespan)})))
         degenerative-entities)))

(defn renderer [entities]
  (let [renderables (filter-entities entities :renderable)]
    (map-entities (fn [id comps]
           (let [render-func (:renderable comps)]
             (render-func comps)
             {id comps}))
         renderables)))

(defn render-particle [components]
  (let [position (:position components)
        lifespan (:lifespan components)]
    (q/fill 20 lifespan)
    (q/ellipse (:x position) (:y position) 10 10)))

(defn setup []
  (q/frame-rate 60)
  (merge {} (entity {:label :particle
                     :velocity (->Vector2D (q/random -1 3) (q/random 1 4))
                     :position (->Vector2D (/ (q/width) 2) 200)
                     :mass 1
                     :lifespan 255
                     :renderable render-particle})))

(defn prog-loop [entities]
  (q/background 240)
  (-> entities
      (system gravity)
      (system wind)
      (system mover)
      (system degeneration)
      (system renderer)))

(defn -main []
  (q/defsketch basic-particles
    :title "Advanced Particles"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
