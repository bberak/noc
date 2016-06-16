(ns basic-particles.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-particles.particle]
            [basic-particles.vector]
            [basic-particles.protocols.particle-operations :refer :all])
  (:import [basic_particles.particle Particle]
           [basic_particles.vector Vector]))

(defn setup []
  (q/frame-rate 60)
  {:particles [(Particle. (Vector. (/ (q/width) 2) 100) (Vector. -0.59 -2.27) (Vector. 0 0.05) 255)]})

(defn update-state [{particles :particles}]
  {:particles (filter is-alive? (map update-particle particles))})

(defn draw-state [{particles :particles}]
  (q/background 240)
  (doseq [p particles] (render p)))

(defn -main []
  (q/defsketch basic-particles
    :title "Basic Particles"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
