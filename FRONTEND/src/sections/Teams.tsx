import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Trophy,
  MapPin,
  User,
  Calendar,
  MoreHorizontal,
  Plus,
  Search,
  Filter,
  ArrowUpDown,
  Loader2,
  Pencil,
  Trash2
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

// --- Types ---
interface Team {
  id: number;
  name: string;
  sport: string;
  coach: string;
  location: string;
  foundedYear: number;
}

export function Teams() {
  const [teams, setTeams] = useState<Team[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // UI State
  const [searchQuery, setSearchQuery] = useState('');
  const [sortField, setSortField] = useState<keyof Team>('name');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);

  // Form State
  const [formData, setFormData] = useState({
    name: '', sport: '', coach: '', location: '', foundedYear: ''
  });

  // --- 1. READ (Load Data) ---
  useEffect(() => {
    fetchTeams();
  }, []);

  const fetchTeams = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/api/teams');

      if (!response.ok) throw new Error(`Server error: ${response.status}`);

      const jsonResponse = await response.json();

      // Распаковка данных с защитой
      let safeTeams = [];
      if (jsonResponse.data && Array.isArray(jsonResponse.data)) {
        safeTeams = jsonResponse.data;
      } else if (Array.isArray(jsonResponse)) {
        safeTeams = jsonResponse;
      }
      setTeams(safeTeams);

    } catch (err: any) {
      console.error("Load error:", err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- 2. CREATE & UPDATE ---
  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      const payload = {
        name: formData.name,
        sport: formData.sport,
        coach: formData.coach,
        location: formData.location,
        foundedYear: parseInt(formData.foundedYear) || 2024
      };

      let url = 'http://localhost:8080/api/teams';
      let method = 'POST';

      // Если есть ID, значит это редактирование (UPDATE)
      if (editingId) {
        url = `http://localhost:8080/api/teams/${editingId}`;
        method = 'PUT';
      }

      const response = await fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        const jsonResponse = await response.json();
        const savedTeam = jsonResponse.data || jsonResponse;

        if (savedTeam && savedTeam.id) {
          if (editingId) {
            // UPDATE: Обновляем команду в списке
            setTeams(prev => prev.map(t => t.id === editingId ? savedTeam : t));
          } else {
            // CREATE: Добавляем новую
            setTeams(prev => [...prev, savedTeam]);
          }
          closeModal();
        }
      } else {
        alert("Error saving team");
      }
    } catch (e) {
      console.error(e);
      alert("Network error");
    } finally {
      setIsSubmitting(false);
    }
  };

  // --- 3. DELETE ---
  const handleDelete = async (id: number) => {
    if (!window.confirm("Delete this team? All players in this team will become Free Agents.")) return;

    try {
      const response = await fetch(`http://localhost:8080/api/teams/${id}`, {
        method: 'DELETE'
      });

      if (response.ok) {
        setTeams(prev => prev.filter(t => t.id !== id));
      } else {
        alert("Failed to delete team");
      }
    } catch (error) {
      console.error("Delete error:", error);
    }
  };

  // --- Helpers ---
  const openAddModal = () => {
    setEditingId(null);
    setFormData({ name: '', sport: '', coach: '', location: '', foundedYear: '' });
    setIsModalOpen(true);
  };

  const openEditModal = (team: Team) => {
    setEditingId(team.id);
    setFormData({
      name: team.name,
      sport: team.sport,
      coach: team.coach,
      location: team.location,
      foundedYear: team.foundedYear.toString()
    });
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setEditingId(null);
  };

  const getSportColor = (sport: string) => {
    const s = (sport || '').toLowerCase();
    if (s.includes('football') || s.includes('soccer')) return 'bg-blue-500/10 text-blue-500 border-blue-500/20';
    if (s.includes('basketball')) return 'bg-orange-500/10 text-orange-500 border-orange-500/20';
    if (s.includes('baseball')) return 'bg-green-500/10 text-green-500 border-green-500/20';
    return 'bg-purple-500/10 text-purple-500 border-purple-500/20';
  };

  // --- Sorting & Filtering ---
  const safeTeams = Array.isArray(teams) ? teams : [];

  const filteredTeams = safeTeams.filter(team =>
      (team.name || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
      (team.sport || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
      (team.location || '').toLowerCase().includes(searchQuery.toLowerCase())
  );

  const sortedTeams = [...filteredTeams].sort((a, b) => {
    const aValue = a[sortField];
    const bValue = b[sortField];
    const comparison = String(aValue || '').localeCompare(String(bValue || ''), undefined, { numeric: true });
    return sortDirection === 'asc' ? comparison : -comparison;
  });

  const handleSort = (field: keyof Team) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDirection('asc');
    }
  };

  if (error) return <div className="p-10 text-red-500">Error: {error}</div>;

  return (
      <div className="space-y-6 p-6">
        {/* HEADER */}
        <div className="flex flex-col sm:flex-row gap-4 justify-between items-start sm:items-center">
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
                placeholder="Search teams..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-9 bg-background/50 border-white/10 text-white placeholder:text-white/40"
            />
          </div>

          <div className="flex gap-2 w-full sm:w-auto">
            <Button variant="outline" className="gap-2 border-white/10 hover:bg-white/5 text-white">
              <Filter className="w-4 h-4" />
              Filter
            </Button>

            <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
              <DialogTrigger asChild>
                <Button onClick={openAddModal} className="gap-2 bg-blue-600 hover:bg-blue-700 text-white">
                  <Plus className="w-4 h-4" />
                  Add Team
                </Button>
              </DialogTrigger>
              <DialogContent className="sm:max-w-[425px] bg-[#1a1b1e] border-white/10 text-white">
                <DialogHeader>
                  <DialogTitle>{editingId ? "Edit Team" : "Add New Team"}</DialogTitle>
                </DialogHeader>
                <form onSubmit={handleSave} className="grid gap-4 py-4">
                  <div className="grid gap-2">
                    <Label>Team Name</Label>
                    <Input
                        value={formData.name}
                        onChange={(e) => setFormData({...formData, name: e.target.value})}
                        className="bg-black/20 border-white/10" required
                    />
                  </div>
                  <div className="grid gap-2">
                    <Label>Sport</Label>
                    <Input
                        value={formData.sport}
                        onChange={(e) => setFormData({...formData, sport: e.target.value})}
                        className="bg-black/20 border-white/10" placeholder="e.g. Football" required
                    />
                  </div>
                  <div className="grid gap-2">
                    <Label>Coach</Label>
                    <Input
                        value={formData.coach}
                        onChange={(e) => setFormData({...formData, coach: e.target.value})}
                        className="bg-black/20 border-white/10" required
                    />
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div className="grid gap-2">
                      <Label>Location</Label>
                      <Input
                          value={formData.location}
                          onChange={(e) => setFormData({...formData, location: e.target.value})}
                          className="bg-black/20 border-white/10" required
                      />
                    </div>
                    <div className="grid gap-2">
                      <Label>Founded</Label>
                      <Input
                          type="number"
                          value={formData.foundedYear}
                          onChange={(e) => setFormData({...formData, foundedYear: e.target.value})}
                          className="bg-black/20 border-white/10" required
                      />
                    </div>
                  </div>
                  <DialogFooter className="mt-4">
                    <Button type="submit" disabled={isSubmitting} className="bg-blue-600 hover:bg-blue-700 w-full text-white">
                      {isSubmitting ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : null}
                      {editingId ? "Update Team" : "Save Team"}
                    </Button>
                  </DialogFooter>
                </form>
              </DialogContent>
            </Dialog>
          </div>
        </div>

        {/* TABLE */}
        <div className="rounded-xl border border-white/10 bg-black/20 overflow-hidden backdrop-blur-sm shadow-xl">
          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left text-gray-300">
              <thead className="text-xs uppercase bg-white/5 text-muted-foreground border-b border-white/5">
              <tr>
                {[
                  { key: 'name', label: 'Team Name' },
                  { key: 'sport', label: 'Sport' },
                  { key: 'coach', label: 'Coach' },
                  { key: 'location', label: 'Location' },
                  { key: 'foundedYear', label: 'Founded' }
                ].map((col) => (
                    <th key={col.key} className="px-6 py-4 font-medium">
                      <button
                          onClick={() => handleSort(col.key as keyof Team)}
                          className="flex items-center gap-1 hover:text-white transition-colors"
                      >
                        {col.label}
                        <ArrowUpDown className="w-3 h-3" />
                      </button>
                    </th>
                ))}
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
              </thead>
              <tbody className="divide-y divide-white/5">
              <AnimatePresence>
                {loading ? (
                    <tr><td colSpan={6} className="px-6 py-12 text-center"><Loader2 className="w-8 h-8 animate-spin mx-auto text-blue-500" /></td></tr>
                ) : sortedTeams.length === 0 ? (
                    <tr><td colSpan={6} className="px-6 py-12 text-center text-muted-foreground">No teams found.</td></tr>
                ) : (
                    sortedTeams.map((team, index) => (
                        <motion.tr
                            key={team.id || index}
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            className="hover:bg-white/5 transition-colors group"
                        >
                          <td className="px-6 py-4 font-medium text-white flex items-center gap-3">
                            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-500/20 to-purple-500/20 flex items-center justify-center border border-white/10 shadow-inner">
                              <Trophy className="w-5 h-5 text-blue-400" />
                            </div>
                            <div>
                              <div className="font-semibold">{team.name}</div>
                              <div className="text-xs text-muted-foreground font-normal">ID: #{team.id}</div>
                            </div>
                          </td>
                          <td className="px-6 py-4">
                        <span className={`px-2.5 py-1 rounded-md text-xs font-medium border ${getSportColor(team.sport)}`}>
                          {team.sport}
                        </span>
                          </td>
                          <td className="px-6 py-4">
                            <div className="flex items-center gap-2">
                              <User className="w-4 h-4 text-muted-foreground" />
                              {team.coach}
                            </div>
                          </td>
                          <td className="px-6 py-4">
                            <div className="flex items-center gap-2">
                              <MapPin className="w-4 h-4 text-muted-foreground" />
                              {team.location}
                            </div>
                          </td>
                          <td className="px-6 py-4">
                            <div className="flex items-center gap-2">
                              <Calendar className="w-4 h-4 text-muted-foreground" />
                              {team.foundedYear}
                            </div>
                          </td>

                          {/* CRUD ACTIONS */}
                          <td className="px-6 py-4 text-right">
                            <DropdownMenu>
                              <DropdownMenuTrigger asChild>
                                <Button variant="ghost" size="icon" className="h-8 w-8 text-muted-foreground hover:text-white">
                                  <MoreHorizontal className="w-4 h-4" />
                                </Button>
                              </DropdownMenuTrigger>
                              <DropdownMenuContent align="end" className="bg-[#1a1b1e] border-white/10 text-white">
                                <DropdownMenuItem onClick={() => openEditModal(team)} className="cursor-pointer hover:bg-white/10">
                                  <Pencil className="w-4 h-4 mr-2" /> Edit
                                </DropdownMenuItem>
                                <DropdownMenuItem onClick={() => handleDelete(team.id)} className="cursor-pointer text-red-500 hover:bg-red-500/10 hover:text-red-400">
                                  <Trash2 className="w-4 h-4 mr-2" /> Delete
                                </DropdownMenuItem>
                              </DropdownMenuContent>
                            </DropdownMenu>
                          </td>
                        </motion.tr>
                    ))
                )}
              </AnimatePresence>
              </tbody>
            </table>
          </div>
        </div>
      </div>
  );
}