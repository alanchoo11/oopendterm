import { motion } from 'framer-motion';
import { 
  LayoutDashboard, 
  Users, 
  Trophy, 
  Calendar, 
  Settings,
  Zap
} from 'lucide-react';
import type { ViewType } from '@/types';

interface SidebarProps {
  activeView: ViewType;
  onViewChange: (view: ViewType) => void;
}

const navItems: { id: ViewType; label: string; icon: React.ElementType }[] = [
  { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { id: 'teams', label: 'Teams', icon: Trophy },
  { id: 'players', label: 'Players', icon: Users },
  { id: 'matches', label: 'Matches', icon: Calendar },
  { id: 'tournaments', label: 'Tournaments', icon: Zap },
];

export function Sidebar({ activeView, onViewChange }: SidebarProps) {
  return (
    <motion.aside
      initial={{ x: -100, opacity: 0 }}
      animate={{ x: 0, opacity: 1 }}
      transition={{ duration: 0.5, ease: [0.4, 0, 0.2, 1] }}
      className="fixed left-0 top-0 h-full w-64 z-50"
    >
      {/* Glassmorphism background */}
      <div className="absolute inset-0 glass-strong border-r border-white/5" />
      
      {/* Subtle gradient overlay */}
      <div className="absolute inset-0 bg-gradient-to-b from-neon-blue/5 via-transparent to-transparent pointer-events-none" />
      
      <div className="relative h-full flex flex-col p-6">
        {/* Logo */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2, duration: 0.5 }}
          className="flex items-center gap-3 mb-12"
        >
          <div className="relative">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-neon-blue to-neon-purple flex items-center justify-center shadow-glow">
              <Trophy className="w-5 h-5 text-white" />
            </div>
            <div className="absolute inset-0 rounded-xl bg-neon-blue/30 blur-lg" />
          </div>
          <div>
            <h1 className="text-lg font-bold text-white tracking-tight">
              Sport<span className="gradient-text">Manager</span>
            </h1>
            <p className="text-xs text-white/40">Pro Edition</p>
          </div>
        </motion.div>

        {/* Navigation */}
        <nav className="flex-1 space-y-2">
          {navItems.map((item, index) => {
            const Icon = item.icon;
            const isActive = activeView === item.id;
            
            return (
              <motion.button
                key={item.id}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: 0.3 + index * 0.1, duration: 0.4 }}
                onClick={() => onViewChange(item.id)}
                className={`
                  relative w-full flex items-center gap-3 px-4 py-3 rounded-xl
                  transition-all duration-300 group
                  ${isActive 
                    ? 'text-white' 
                    : 'text-white/50 hover:text-white hover:bg-white/5'
                  }
                `}
              >
                {/* Active indicator */}
                {isActive && (
                  <motion.div
                    layoutId="activeNav"
                    className="absolute inset-0 rounded-xl bg-gradient-to-r from-neon-blue/20 to-transparent border-l-2 border-neon-blue"
                    transition={{ type: 'spring', bounce: 0.2, duration: 0.6 }}
                  />
                )}
                
                {/* Glow effect on hover */}
                <div className={`
                  absolute inset-0 rounded-xl opacity-0 group-hover:opacity-100 transition-opacity duration-300
                  ${!isActive && 'bg-gradient-to-r from-white/5 to-transparent'}
                `} />
                
                <span className="relative">
                  <Icon className={`
                    w-5 h-5 transition-all duration-300
                    ${isActive ? 'text-neon-blue' : 'group-hover:text-neon-blue/70'}
                  `} />
                </span>
                <span className="relative font-medium text-sm">{item.label}</span>
                
                {/* Hover glow */}
                <div className={`
                  absolute right-2 w-1.5 h-1.5 rounded-full bg-neon-blue
                  transition-all duration-300
                  ${isActive ? 'opacity-100 shadow-glow' : 'opacity-0 group-hover:opacity-50'}
                `} />
              </motion.button>
            );
          })}
        </nav>

        {/* Bottom section */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.8, duration: 0.5 }}
          className="pt-6 border-t border-white/5"
        >
          <button className="flex items-center gap-3 px-4 py-3 rounded-xl text-white/50 hover:text-white hover:bg-white/5 transition-all duration-300 group w-full">
            <Settings className="w-5 h-5 group-hover:rotate-90 transition-transform duration-500" />
            <span className="font-medium text-sm">Settings</span>
          </button>
          
          {/* Version info */}
          <div className="mt-4 px-4 text-xs text-white/20">
            Prod by Alan
          </div>
        </motion.div>
      </div>
    </motion.aside>
  );
}
