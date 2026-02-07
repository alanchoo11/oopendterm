import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Sidebar } from '@/components/Sidebar';
import { Header } from '@/components/Header';
import { Dashboard } from '@/sections/Dashboard';
import { Teams } from '@/sections/Teams';
import { Players } from '@/sections/Players';
import type { ViewType } from '@/types';

function App() {
  const [activeView, setActiveView] = useState<ViewType>('dashboard');

  const viewTitles: Record<ViewType, { title: string; subtitle: string }> = {
    dashboard: {
      title: 'Dashboard',
      subtitle: 'Overview of your sport management system',
    },
    teams: {
      title: 'Teams',
      subtitle: 'Manage your teams and organizations',
    },
    players: {
      title: 'Players',
      subtitle: 'Manage players and their statistics',
    },
    matches: {
      title: 'Matches',
      subtitle: 'Schedule and track matches',
    },
    tournaments: {
      title: 'Tournaments',
      subtitle: 'Organize and manage tournaments',
    },
  };

  const renderView = () => {
    switch (activeView) {
      case 'dashboard':
        return <Dashboard />;
      case 'teams':
        return <Teams />;
      case 'players':
        return <Players />;
      case 'matches':
        return (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="flex items-center justify-center h-96"
          >
            <div className="text-center">
              <div className="w-20 h-20 rounded-2xl bg-neon-purple/20 flex items-center justify-center mx-auto mb-4">
                <span className="text-4xl">🏆</span>
              </div>
              <h3 className="text-xl font-semibold text-white mb-2">Coming Soon</h3>
              <p className="text-white/50">Matches management will be available in the next update.</p>
            </div>
          </motion.div>
        );
      case 'tournaments':
        return (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="flex items-center justify-center h-96"
          >
            <div className="text-center">
              <div className="w-20 h-20 rounded-2xl bg-neon-orange/20 flex items-center justify-center mx-auto mb-4">
                <span className="text-4xl">⚡</span>
              </div>
              <h3 className="text-xl font-semibold text-white mb-2">Coming Soon</h3>
              <p className="text-white/50">Tournaments management will be available in the next update.</p>
            </div>
          </motion.div>
        );
      default:
        return <Dashboard />;
    }
  };

  return (
    <div className="min-h-screen bg-gradient-dark bg-grid">
      {/* Animated background particles */}
      <div className="fixed inset-0 pointer-events-none overflow-hidden">
        {[...Array(20)].map((_, i) => (
          <motion.div
            key={i}
            className="absolute w-1 h-1 bg-neon-blue/30 rounded-full"
            initial={{
              x: Math.random() * window.innerWidth,
              y: Math.random() * window.innerHeight,
            }}
            animate={{
              y: [null, Math.random() * window.innerHeight],
              opacity: [0.2, 0.5, 0.2],
            }}
            transition={{
              duration: 10 + Math.random() * 20,
              repeat: Infinity,
              ease: 'linear',
            }}
          />
        ))}
      </div>

      {/* Noise overlay */}
      <div className="fixed inset-0 pointer-events-none noise-overlay opacity-50" />

      {/* Sidebar */}
      <Sidebar activeView={activeView} onViewChange={setActiveView} />

      {/* Main Content */}
      <div className="ml-64 min-h-screen flex flex-col">
        {/* Header */}
        <Header 
          title={viewTitles[activeView].title} 
          subtitle={viewTitles[activeView].subtitle} 
        />

        {/* Content Area */}
        <main className="flex-1 overflow-auto">
          <AnimatePresence mode="wait">
            <motion.div
              key={activeView}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              transition={{ duration: 0.3 }}
            >
              {renderView()}
            </motion.div>
          </AnimatePresence>
        </main>
      </div>
    </div>
  );
}

export default App;
