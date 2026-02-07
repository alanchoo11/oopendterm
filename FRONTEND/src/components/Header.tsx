import { motion } from 'framer-motion';
import { Search, Bell, User } from 'lucide-react';
import { Input } from '@/components/ui/input';

interface HeaderProps {
  title: string;
  subtitle?: string;
}

export function Header({ title, subtitle }: HeaderProps) {
  return (
    <motion.header
      initial={{ opacity: 0, y: -20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, delay: 0.2 }}
      className="flex items-center justify-between py-6 px-8"
    >
      {/* Title section */}
      <div>
        <motion.h2
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.5, delay: 0.3 }}
          className="text-2xl font-bold text-white"
        >
          {title}
        </motion.h2>
        {subtitle && (
          <motion.p
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, delay: 0.4 }}
            className="text-sm text-white/50 mt-1"
          >
            {subtitle}
          </motion.p>
        )}
      </div>

      {/* Right section */}
      <div className="flex items-center gap-6">
        {/* Search */}
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.4 }}
          className="relative"
        >
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-white/40" />
          <Input
            type="text"
            placeholder="Search..."
            className="w-64 pl-10 bg-white/5 border-white/10 text-white placeholder:text-white/30 
                       focus:border-neon-blue/50 focus:ring-neon-blue/20 rounded-xl
                       transition-all duration-300"
          />
          <div className="absolute inset-0 rounded-xl bg-neon-blue/5 opacity-0 focus-within:opacity-100 transition-opacity pointer-events-none" />
        </motion.div>

        {/* Notifications */}
        <motion.button
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.5 }}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          className="relative p-2.5 rounded-xl bg-white/5 border border-white/10 
                     hover:bg-white/10 hover:border-neon-blue/30 transition-all duration-300 group"
        >
          <Bell className="w-5 h-5 text-white/60 group-hover:text-neon-blue transition-colors" />
          <span className="absolute -top-1 -right-1 w-5 h-5 rounded-full bg-neon-orange text-xs 
                           flex items-center justify-center text-white font-medium shadow-glow-orange">
            3
          </span>
        </motion.button>

        {/* Profile */}
        <motion.button
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.6 }}
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.98 }}
          className="flex items-center gap-3 px-4 py-2 rounded-xl bg-white/5 border border-white/10 
                     hover:bg-white/10 hover:border-neon-blue/30 transition-all duration-300"
        >
          <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-neon-blue to-neon-purple 
                          flex items-center justify-center shadow-glow">
            <User className="w-4 h-4 text-white" />
          </div>
          <div className="text-left">
            <p className="text-sm font-medium text-white">Admin</p>
            <p className="text-xs text-white/40">Manager</p>
          </div>
        </motion.button>
      </div>
    </motion.header>
  );
}
