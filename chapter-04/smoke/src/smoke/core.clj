(ns smoke.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-particles.records.vector2d :refer :all]
            [basic-ces.core :refer :all]
            [smoke.systems :refer :all]))

(def puff (ref nil))
(def initial-lifespan 1800)

(defn setup []
  (q/frame-rate 30)
  (q/blend-mode :add)
  (dosync (ref-set puff (q/load-image "puff.png")))
  (merge {} (entity {:label :particle-system
                     :position (->Vector2D (/ (q/width) 2) 500)
                     :particle-emitter true})))

(defn render-particle [components]
  (let [position (:position components)
        lifespan (:lifespan components)]
    (q/rect-mode :center)
    (q/image-mode :center)
    (q/tint 60 (q/map-range lifespan 0 initial-lifespan 0 255))
    (q/with-translation [(:x position) (:y position)]
      (q/image @puff 0 0 40 40))))

(defn create-particle-entity [position]
  (entity {:label :particle
           :velocity (->Vector2D (-> (q/random-gaussian) (* 0.13)) (-> (q/random-gaussian) (* 0.13) (- 1)))
           :position position
           :mass 1
           :lifespan initial-lifespan
           :renderable render-particle}))

(defn prog-loop [entities]
  (q/background 30)
  (-> entities
      (particle-emitter create-particle-entity)
      (wind)
      (attract-and-repel-with-mouse)
      (mover)
      (degeneration)
      (renderer)))

(defn -main []
  (q/defsketch smoke
    :title "Smoke"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :renderer :p2d
    :middleware [m/fun-mode]))
